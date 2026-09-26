package com.moira.wimb.domain.post.dto.request

data class PostAddRequest(
    // 기본 정보
    val categoryId: Long,
    val title: String,
    val type: String,
    val visibility: String,

    // 유형별 정보
    val snippet: PostSnippetAddRequest?,
    val normal: PostNormalAddRequest?,

    // 태그 정보
    val tags: List<PostTagAddRequest>?
)
