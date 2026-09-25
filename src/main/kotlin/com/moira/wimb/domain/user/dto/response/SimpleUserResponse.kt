package com.moira.wimb.domain.user.dto.response

data class SimpleUserResponse(
    val userId: String,
    val loginId: String,
    val email: String,
    val role: String,
    val imageUrl: String?
)