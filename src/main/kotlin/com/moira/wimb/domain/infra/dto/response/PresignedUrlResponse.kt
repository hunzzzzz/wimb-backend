package com.moira.wimb.domain.infra.dto.response

data class PresignedUrlResponse(
    val fileId: String,
    val items: List<PresignedUrlItemResponse>
)
