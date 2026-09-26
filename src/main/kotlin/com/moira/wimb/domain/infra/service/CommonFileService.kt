package com.moira.wimb.domain.infra.service

import com.moira.wimb.domain.infra.dto.request.FileUploadResultRequest
import com.moira.wimb.domain.infra.dto.request.PresignedUrlRequest
import com.moira.wimb.domain.infra.dto.response.PresignedUrlItemResponse
import com.moira.wimb.domain.infra.dto.response.PresignedUrlResponse
import com.moira.wimb.domain.infra.entity.ApiType
import com.moira.wimb.domain.infra.entity.CommonFile
import com.moira.wimb.domain.infra.entity.FileIdentifier
import com.moira.wimb.domain.infra.entity.FileStatus
import com.moira.wimb.domain.infra.mapper.CommonFileMapper
import com.moira.wimb.global.exception.CommonException
import com.moira.wimb.global.exception.ErrorCode
import com.moira.wimb.global.utility.CommonUtils
import com.moira.wimb.global.utility.CommonVariables.FILE_ID_PREFIX
import com.moira.wimb.global.utility.CommonVariables.YES
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration

@Service
class CommonFileService(
    // repository
    private val commonFileMapper: CommonFileMapper,

    // utility
    private val apiResultLogger: ApiResultLogger,
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner,

    // variables
    @Value($$"${cloud.aws.s3.bucketName}")
    private val bucketName: String,
    @Value($$"${cloud.aws.region.static}")
    private val regionName: String
) {
    private val log = KotlinLogging.logger {}

    /**
     * AWS S3 Presigned Url 발급
     */
    @Transactional
    fun presignedUrl(userId: String, request: PresignedUrlRequest): PresignedUrlResponse {
        // 1. 유효성 검사
        if (!CommonUtils.isValidEnum<FileIdentifier>(request.identifier)) {
            throw CommonException(ErrorCode.INVALID_FILE_IDENTIFIER)
        }
        if (request.items.map { it.representYn }.count { it == YES } > 1) {
            throw CommonException(ErrorCode.INVALID_REPRESENT_YN)
        }

        // 2. 파라미터 세팅
        val identifier = FileIdentifier.valueOf(request.identifier)
        val files = mutableListOf<CommonFile>()
        val fileId = CommonUtils.createRandomId(FILE_ID_PREFIX)
        val items = mutableListOf<PresignedUrlItemResponse>()

        request.items.forEachIndexed { index, itemRequest ->
            val fileSeqNo = index + 1
            val s3Key = CommonUtils.getS3Key(
                identifier = identifier,
                fileId = fileId,
                fileSeqNo = fileSeqNo,
                originalFileName = itemRequest.originalFileName
            )
            val fileUrl = "https://${bucketName}.s3.${regionName}.amazonaws.com/${s3Key}"

            // 3. CommonFile 객체 생성
            val commonFile = CommonFile(
                fileId = fileId,
                fileSeqNo = fileSeqNo,
                userId = userId,
                fileUrl = fileUrl,
                originalFileName = itemRequest.originalFileName,
                size = itemRequest.size,
                contentType = itemRequest.contentType,
                identifier = identifier.name,
                s3Key = s3Key,
                representYn = itemRequest.representYn,
            )

            // 4. ApiResultLogger > start
            val apiResultSeqNo = apiResultLogger.logStart(type = ApiType.S3_PRESIGNED_URL, s3Key = s3Key)

            // 5. Presigned Url 발급
            val presignedUrl = runCatching {
                val putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(itemRequest.contentType)
                    .build()

                val presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10)) // 10분
                    .putObjectRequest(putObjectRequest)
                    .build()

                val presignedUrl = s3Presigner.presignPutObject(presignRequest).url().toString()

                log.info {
                    """
                    [AWS S3] PresignedUrl 발급 성공!
                    - fileId: ${commonFile.fileId}
                    - fileSeqNo: ${commonFile.fileSeqNo}
                    - s3Key: ${commonFile.s3Key}
                    """.trimIndent()
                }

                presignedUrl
            }.getOrElse {
                log.info {
                    """
                    [AWS S3] PresignedUrl 발급 실패!
                    - fileId: ${commonFile.fileId}
                    - fileSeqNo: ${commonFile.fileSeqNo}
                    - s3Key: ${commonFile.s3Key}
                    - message: ${it.message}
                    """.trimIndent()
                }

                // ApiResultLogger > fail
                apiResultLogger.logFail(apiResultSeqNo, it.message ?: "")

                throw CommonException(ErrorCode.S3_PRESIGNED_URL_CREATE_FAILED)
            }

            // 6. ApiResultLgger > success
            apiResultLogger.logSuccess(apiResultSeqNo)

            // 7. PresignedUrlItemResponse 객체 생성
            val presignedUrlItemResponse = PresignedUrlItemResponse(
                fileSeqNo = fileSeqNo,
                presignedUrl = presignedUrl,
                fileUrl = fileUrl,
                representYn = commonFile.representYn,
            )

            // 8. 각 리스트에 add
            files.add(commonFile)
            items.add(presignedUrlItemResponse)
        }

        // 9. CommonFile 저장
        commonFileMapper.insertCommonFileBulk(files)

        // 10. PresignedUrlResponse 객체 생성 후 리턴
        return PresignedUrlResponse(
            fileId = fileId,
            items = items
        )
    }

    /**
     * 파일 업로드 결과 전송
     */
    @Transactional
    fun uploadResult(userId: String, request: FileUploadResultRequest) {
        request.items.forEach { itemRequest ->
            // 1. CommonFile의 status 조회
            val status = commonFileMapper.selectStatus(
                fileId = request.fileId,
                fileSeqNo = itemRequest.fileSeqNo,
                userId = userId,
            )

            if (status != null && status == FileStatus.PENDING.name) {
                val isSuccess = itemRequest.successYn == YES
                val newStatus = if (isSuccess) FileStatus.UPLOAD_SUCCESS else FileStatus.FAIL

                // 2. CommonFile 수정 (PENDING일 때만)
                // ApiResult는 INSERT/UPDATE하지 않는다. (사유: 파일 업로드의 주체는 프론트이기 때문)
                if (isSuccess) {
                    commonFileMapper.updateStatusAndUploadedAt(
                        fileId = request.fileId,
                        fileSeqNo = itemRequest.fileSeqNo,
                        userId = userId,
                        status = newStatus.name
                    )
                } else {
                    commonFileMapper.updateStatus(
                        fileId = request.fileId,
                        fileSeqNo = itemRequest.fileSeqNo,
                        userId = userId,
                        status = newStatus.name
                    )
                }
            }
        }
    }
}