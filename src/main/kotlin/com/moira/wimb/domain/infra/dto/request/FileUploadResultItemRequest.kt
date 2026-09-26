package com.moira.wimb.domain.infra.dto.request

data class FileUploadResultItemRequest(
    val fileSeqNo: Int,
    val successYn: String,
    val failMessage: String?
)
