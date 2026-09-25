package com.moira.wimb.global.mail.event

sealed class MailEvent(
    open val email: String,
    open val apiResultSeqNo: Long
)