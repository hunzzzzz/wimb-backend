package com.moira.wimb.domain.infra.dto.request

data class IdentificationCodeConfirmRequest(
    val email: String,
    val code: String,
    val purpose: String
)
