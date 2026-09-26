package com.moira.wimb.domain.post.dto.request

data class PostNormalAddRequest(
    val content: String,
    val plainContent: String,
    val fileId: String?,
)
