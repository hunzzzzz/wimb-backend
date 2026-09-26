package com.moira.wimb.domain.infra.dto.request

data class PresignedUrlItemRequest(
    val originalFileName: String,
    val size: Long,
    val contentType: String,
    val representYn: String,
)
