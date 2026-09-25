package com.moira.wimb.domain.post.entity

import com.moira.wimb.domain.post.dto.request.PostSnippetAddRequest
import java.time.LocalDateTime

data class PostSnippet(
    val postId: String,
    val code: String,
    val description: String,
    val language: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun create(postId: String, request: PostSnippetAddRequest): PostSnippet {
            return PostSnippet(
                postId = postId,
                code = request.code,
                description = request.description,
                language = request.language
            )
        }
    }
}