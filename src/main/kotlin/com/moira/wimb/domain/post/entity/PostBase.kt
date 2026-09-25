package com.moira.wimb.domain.post.entity

import java.time.LocalDateTime

data class PostBase(
    val postId: String,
    val categoryId: Long,
    val userId: String,
    val title: String,
    val status: String,
    val type: String,
    val visibility: String,
    val viewCount: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
