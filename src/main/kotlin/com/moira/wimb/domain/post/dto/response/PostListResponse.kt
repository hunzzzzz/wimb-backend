package com.moira.wimb.domain.post.dto.response

import java.time.LocalDateTime

data class PostListResponse(
    val postId: String,
    val categoryId: Long,
    val type: String,
    val title: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
