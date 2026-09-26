package com.moira.wimb.domain.infra.entity

import com.moira.wimb.global.utility.CommonVariables.MAIL_SEND_EXPIRY_MIN
import java.time.LocalDateTime

data class Identification(
    val seqNo: Long = -1L,
    val email: String,
    val code: String,
    val status: String = IdentificationStatus.PENDING.name,
    val purpose: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime = LocalDateTime.now().plusMinutes(MAIL_SEND_EXPIRY_MIN),
    val failCount: Int = 0,
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun create(email: String, code: String, purpose: IdentificationPurpose): Identification {
            return Identification(
                email = email,
                code = code,
                purpose = purpose.name,
            )
        }
    }
}