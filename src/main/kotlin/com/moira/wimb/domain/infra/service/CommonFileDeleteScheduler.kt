package com.moira.wimb.domain.infra.service

import com.moira.wimb.domain.infra.entity.ApiType
import com.moira.wimb.domain.infra.mapper.CommonFileMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CommonFileDeleteScheduler(
    // repository
    private val commonFileMapper: CommonFileMapper,

    // service
    private val commonFileService: CommonFileService,

    // utility
    private val apiResultLogger: ApiResultLogger
) {
    private val log = KotlinLogging.logger {}

    /**
     * 실제 파일 삭제
     */
    @Scheduled(cron = "0 0/30 * * * *") // 30분 간격
    fun delete() {
        // 1. 삭제 대상인 CommonFile 목록을 조회
        val files = commonFileMapper.selectAllForDeleteScheduler()
        var successCnt = 0
        var failedCnt = 0

        log.info {
            """
            
            [CommonFileDeleteScheduler] 스케줄러 작동 시작.
            - 삭제 대상: ${files.size}건
            """.trimIndent()
        }

        // 2. 각 파일 별로 트랜잭션을 분리하여 삭제 (S3 삭제 + DB 삭제)
        files.forEach {
            val apiResultSeqNo = apiResultLogger.logStart(type = ApiType.S3_DELETE, s3Key = it.s3Key)

            runCatching {
                commonFileService.delete(it)
            }.onSuccess {
                apiResultLogger.logSuccess(apiResultSeqNo)
                successCnt++
            }.onFailure { e ->
                apiResultLogger.logFail(apiResultSeqNo, e.message ?: "")
                failedCnt++
            } // 다음 파일에 영향을 주지 않도록 Exception throw하지 않음
        }

        log.info {
            """
            
            [CommonFileDeleteScheduler] 스케줄러 종료.
            - 삭제 대상: ${files.size}건
            - 삭제 성공: ${successCnt}건
            - 삭제 실패: ${failedCnt}건
            """.trimIndent()
        }
    }
}