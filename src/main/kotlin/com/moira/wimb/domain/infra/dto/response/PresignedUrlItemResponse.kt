package com.moira.wimb.domain.infra.dto.response

data class PresignedUrlItemResponse(
    val fileSeqNo: Int,
    val presignedUrl: String,
    val fileUrl: String,
    val representYn: String,
)
