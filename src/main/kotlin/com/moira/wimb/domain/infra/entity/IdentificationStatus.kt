package com.moira.wimb.domain.infra.entity

enum class IdentificationStatus {
    PENDING, MAIL_SENT, VERIFIED, USED, // 정흐름
    MAIL_SENT_FAILED, CONFIRM_LIMIT_EXCEEDED, // 예외
}