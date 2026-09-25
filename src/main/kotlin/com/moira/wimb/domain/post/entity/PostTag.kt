package com.moira.wimb.domain.post.entity

import java.time.LocalDateTime

data class PostTag(
    val seqNo: Long = -1L,
    val postId: String,
    val name: String,
    val sortOrder: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)