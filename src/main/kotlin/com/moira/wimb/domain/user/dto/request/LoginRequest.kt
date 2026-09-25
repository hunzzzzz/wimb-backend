package com.moira.wimb.domain.user.dto.request

data class LoginRequest(
    val loginId: String,
    val password: String
)