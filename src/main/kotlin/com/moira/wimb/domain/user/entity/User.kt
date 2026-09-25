package com.moira.wimb.domain.user.entity

import com.moira.wimb.domain.user.dto.request.SignupRequest
import java.time.LocalDateTime

data class User(
    var userId: String,
    var loginId: String,
    var password: String,
    var email: String,
    var role: String = UserRole.MEMBER.name,
    var status: String = UserStatus.ACTIVE.name,
    var fileId: String? = null,
    var description: String? = null,
    var visitCount: Int = 0,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    var deletedAt: LocalDateTime? = null
) {
    companion object {
        fun create(userId: String, request: SignupRequest, encodedPassword: String): User {
            return User(
                userId = userId,
                loginId = request.loginId,
                password = encodedPassword,
                email = request.email,
            )
        }
    }
}