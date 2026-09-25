package com.moira.wimb.domain.infra.entity

import java.time.LocalDateTime

data class ApiResult(
    val seqNo: Long = -1L,
    val type: String,
    val email: String? = null, // type이 MAIL_SEND일 경우에만
    val s3Key: String? = null, // type이 PRESIGNED_URL일 경우에만
    val status: String = ApiStatus.PENDING.name,
    val failMessage: String? = null,
    val requestedAt: LocalDateTime = LocalDateTime.now(),
    val respondedAt: LocalDateTime? = null,
) {
    companion object {
        fun create(type: ApiType, email: String? = null, s3Key: String? = null): ApiResult {
            return ApiResult(
                type = type.name,
                email = email,
                s3Key = s3Key,
            )
        }
    }
}