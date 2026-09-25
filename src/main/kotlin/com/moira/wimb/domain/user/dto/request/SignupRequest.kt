package com.moira.wimb.domain.user.dto.request

data class SignupRequest(
    val loginId: String,
    val password: String,
    val email: String
)