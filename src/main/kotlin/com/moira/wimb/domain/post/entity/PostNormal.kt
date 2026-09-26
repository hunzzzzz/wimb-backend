package com.moira.wimb.domain.post.entity

import com.moira.wimb.domain.post.dto.request.PostNormalAddRequest
import java.time.LocalDateTime

data class PostNormal(
    val postId: String,
    val content: String,
    val plainContent: String,
    val fileId: String?,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun create(postId: String, request: PostNormalAddRequest): PostNormal {
            return PostNormal(
                postId = postId,
                content = request.content,
                plainContent = request.plainContent,
                fileId = request.fileId
            )
        }
    }
}