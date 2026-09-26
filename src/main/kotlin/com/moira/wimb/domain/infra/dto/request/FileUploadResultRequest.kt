package com.moira.wimb.domain.infra.dto.request

data class FileUploadResultRequest(
    val fileId: String,
    val items: List<FileUploadResultItemRequest>
)
