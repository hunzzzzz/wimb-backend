package com.moira.wimb.domain.post.entity

import com.moira.wimb.domain.post.dto.request.PostTagAddRequest
import java.time.LocalDateTime

data class PostTag(
    val seqNo: Long = -1L,
    val postId: String,
    val name: String,
    val sortOrder: Int,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun create(postId: String, request: PostTagAddRequest): PostTag {
            return PostTag(
                postId = postId,
                name = request.name,
                sortOrder = request.sortOrder,
            )
        }
    }
}