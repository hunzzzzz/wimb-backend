package com.moira.wimb.domain.infra.dto.request

data class IdentificationMailSendRequest(
    val email: String,
    val purpose: String
)
