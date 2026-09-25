package com.moira.wimb.domain.infra.service

import com.moira.wimb.domain.infra.entity.ApiResult
import com.moira.wimb.domain.infra.entity.ApiStatus
import com.moira.wimb.domain.infra.entity.ApiType
import com.moira.wimb.domain.infra.mapper.ApiResultMapper
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class ApiResultLogger(
    private val apiResultMapper: ApiResultMapper
) {
    /**
     * 외부 API 기록 시작 (PENDING)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun logStart(type: ApiType, email: String? = null, s3Key: String? = null): Long {
        val apiResult = ApiResult.create(type = type, email = email, s3Key = s3Key)
        apiResultMapper.insertApiResult(apiResult)

        return apiResult.seqNo
    }

    /**
     * 외부 API 성공 (SUCCESS)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun logSuccess(seqNo: Long) {
        apiResultMapper.updateStatus(seqNo, ApiStatus.SUCCESS.name)
    }

    /**
     * 외부 API 실패 (FAIL)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun logFail(seqNo: Long, failMessage: String) {
        apiResultMapper.updateStatusAndFailMessage(seqNo, ApiStatus.FAIL.name, failMessage)
    }
}