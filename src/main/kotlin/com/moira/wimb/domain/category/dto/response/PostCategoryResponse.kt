package com.moira.wimb.domain.category.dto.response

data class PostCategoryResponse(
    val postCategoryId: Long,
    val name: String,
    val sortOrder: Int,
    val postCount: Long
)