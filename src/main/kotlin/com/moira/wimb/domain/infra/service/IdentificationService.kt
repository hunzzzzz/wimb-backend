package com.moira.wimb.domain.infra.service

import com.moira.wimb.domain.infra.dto.request.IdentificationCodeConfirmRequest
import com.moira.wimb.domain.infra.dto.request.IdentificationMailSendRequest
import com.moira.wimb.domain.infra.entity.ApiType
import com.moira.wimb.domain.infra.entity.Identification
import com.moira.wimb.domain.infra.entity.IdentificationPurpose
import com.moira.wimb.domain.infra.entity.IdentificationStatus
import com.moira.wimb.domain.infra.mapper.ApiResultMapper
import com.moira.wimb.domain.infra.mapper.IdentificationMapper
import com.moira.wimb.domain.user.mapper.UserMapper
import com.moira.wimb.global.exception.CommonException
import com.moira.wimb.global.exception.ErrorCode
import com.moira.wimb.global.mail.event.IdentificationMailEvent
import com.moira.wimb.global.utility.CommonUtils
import com.moira.wimb.global.utility.CommonVariables.MAIL_EXTRA_MIN_AFTER_VERIFY
import com.moira.wimb.global.utility.CommonVariables.MAIL_IDENTIFICATION_CONFIRM_MAX_COUNT
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IdentificationService(
    // mapper
    private val apiResultMapper: ApiResultMapper,
    private val identificationMapper: IdentificationMapper,
    private val userMapper: UserMapper,

    // service
    private val identificationTransactionService: IdentificationTransactionService,

    // utility
    private val apiResultLogger: ApiResultLogger,
    private val eventPublisher: ApplicationEventPublisher
) {
    /**
     * 인증번호 메일 전송
     */
    @Transactional
    fun sendCode(request: IdentificationMailSendRequest) {
        // 1. 유효성 검사
        if (!CommonUtils.isValidEnum<IdentificationPurpose>(request.purpose))
            throw CommonException(ErrorCode.INVALID_IDENTIFICATION_PURPOSE)
        val purpose = IdentificationPurpose.valueOf(request.purpose)

        when (purpose) {
            IdentificationPurpose.SIGNUP, IdentificationPurpose.CHANGE_EMAIL -> {
                if (userMapper.selectEmailChk(request.email))
                    throw CommonException(ErrorCode.EMAIL_EXISTS)
            }

            IdentificationPurpose.CHANGE_PASSWORD -> {
                if (!userMapper.selectEmailChk(request.email))
                    throw CommonException(ErrorCode.USER_NOT_FOUND)
            }
        }
        if (apiResultMapper.selectRepeatInOneMinuteChk(request.email))
            throw CommonException(ErrorCode.ONLY_ONE_MAIL_IN_MINUTE)

        // 2. 변수 세팅
        val email = request.email
        val code = CommonUtils.generateAuthCode()

        // 3. Identification 저장 (status: PENDING)
        val identificationSeqNo = Identification.create(email, code, purpose)
            .let { identificationMapper.insertIdentification(it); it.seqNo }

        // 4. ApiResult 저장 (status: PENDING)
        val apiResultSeqNo = apiResultLogger.logStart(type = ApiType.MAIL_SEND, email = email)

        // 5. 메일 전송 -> 커밋 후에 이벤트 발행
        eventPublisher.publishEvent(
            IdentificationMailEvent(
                email = email,
                apiResultSeqNo = apiResultSeqNo,
                code = code,
                identificationSeqNo = identificationSeqNo,
            )
        )
    }

    /**
     * 인증번호 확인
     */
    @Transactional
    fun confirmCode(request: IdentificationCodeConfirmRequest) {
        // 1. 유효성 검사1
        if (!CommonUtils.isValidEnum<IdentificationPurpose>(request.purpose))
            throw CommonException(ErrorCode.INVALID_IDENTIFICATION_PURPOSE)

        // 2. 가장 최근의 Identification 1건 조회 (expiresAt > now)
        val identification = identificationMapper.selectRecentIdentification(
            email = request.email,
            purpose = IdentificationPurpose.valueOf(request.purpose).name
        ) ?: throw CommonException(ErrorCode.EXPIRED_IDENTIFICATION_CODE)

        // 3. 유효성 검사2
        when (IdentificationStatus.valueOf(identification.status)) {
            IdentificationStatus.VERIFIED, IdentificationStatus.USED
                -> throw CommonException(ErrorCode.ALREADY_VERIFIED)

            IdentificationStatus.PENDING, IdentificationStatus.MAIL_SENT_FAILED, IdentificationStatus.CONFIRM_LIMIT_EXCEEDED
                -> throw CommonException(ErrorCode.FORBIDDEN)

            else -> {}
        }
        if (identification.failCount >= MAIL_IDENTIFICATION_CONFIRM_MAX_COUNT) {
            identificationTransactionService.updateStatus(
                identificationSeqNo = identification.seqNo,
                status = IdentificationStatus.CONFIRM_LIMIT_EXCEEDED
            )
            throw CommonException(ErrorCode.CODE_CONFIRM_COUNT_EXCEEDED)
        }
        if (request.code != identification.code) {
            identificationTransactionService.incrementFailCount(identification.seqNo)
            throw CommonException(ErrorCode.WRONG_IDENTIFICATION_CODE)
        }

        // 4. Identification의 status를 VERIFIED로 변경 + expiresAt을 넉넉히 연장
        identificationMapper.updateStatusAndExpiresAt(
            seqNo = identification.seqNo,
            status = IdentificationStatus.VERIFIED.name,
            expiresAt = identification.expiresAt.plusMinutes(MAIL_EXTRA_MIN_AFTER_VERIFY)
        )
    }
}