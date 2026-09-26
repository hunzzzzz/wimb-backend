package com.moira.wimb.domain.post.entity

import com.moira.wimb.domain.post.dto.request.PostAddRequest
import java.time.LocalDateTime

data class PostBase(
    val postId: String,
    val categoryId: Long,
    val userId: String,
    val title: String,
    val status: String = PostStatus.ACTIVE.name,
    val type: String,
    val visibility: String,
    val viewCount: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null
) {
    companion object {
        fun create(postId: String, userId: String, request: PostAddRequest): PostBase {
            return PostBase(
                postId = postId,
                categoryId = request.categoryId,
                userId = userId,
                title = request.title,
                type = request.type,
                visibility = request.visibility
            )
        }
    }
}