package com.moira.wimb.global.mail.event

data class IdentificationMailEvent(
    override val email: String,
    override val apiResultSeqNo: Long,
    val code: String,
    val identificationSeqNo: Long
) : MailEvent(email, apiResultSeqNo)