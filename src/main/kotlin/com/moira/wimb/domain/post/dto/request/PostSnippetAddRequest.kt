package com.moira.wimb.domain.post.dto.request

data class PostSnippetAddRequest(
    val code: String,
    val description: String,
    val language: String
)
