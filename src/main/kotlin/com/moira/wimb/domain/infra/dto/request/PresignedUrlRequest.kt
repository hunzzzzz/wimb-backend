package com.moira.wimb.domain.infra.dto.request

data class PresignedUrlRequest(
    val identifier: String,
    val items: List<PresignedUrlItemRequest>
)
