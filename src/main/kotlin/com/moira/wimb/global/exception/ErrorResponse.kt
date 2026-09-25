package com.moira.wimb.global.exception

import java.time.LocalDateTime

data class ErrorResponse(
    val errCode: String,
    val errName: String,
    val errMsg: String,
    val time: LocalDateTime = LocalDateTime.now()
)
