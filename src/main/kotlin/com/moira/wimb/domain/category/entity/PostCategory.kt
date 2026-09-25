package com.moira.wimb.domain.category.entity

import com.moira.wimb.domain.category.dto.request.PostCategoryAddRequest
import java.time.LocalDateTime

data class PostCategory(
    var postCategoryId: Long? = null,
    var userId: String,
    var name: String,
    var sortOrder: Int,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun create(userId: String, sortOrder: Int, request: PostCategoryAddRequest): PostCategory {
            return PostCategory(
                userId = userId,
                name = request.name,
                sortOrder = sortOrder,
            )
        }
    }
}