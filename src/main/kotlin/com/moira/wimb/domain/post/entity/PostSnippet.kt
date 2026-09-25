package com.moira.wimb.domain.post.entity

import java.time.LocalDateTime

data class PostSnippet(
    val postId: String,
    val code: String,
    val description: String,
    val language: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
