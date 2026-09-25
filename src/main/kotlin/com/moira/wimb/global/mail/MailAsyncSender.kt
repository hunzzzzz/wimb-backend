package com.moira.wimb.global.mail

import com.moira.wimb.domain.infra.service.ApiResultLogger
import com.moira.wimb.global.exception.CommonException
import com.moira.wimb.global.exception.ErrorCode
import com.moira.wimb.global.mail.event.IdentificationMailEvent
import com.moira.wimb.global.mail.event.MailEvent
import com.moira.wimb.global.utility.CommonVariables.MAIL_SUBJECT_IDENTIFICATION
import com.moira.wimb.global.utility.CommonVariables.MAIL_TEMPLATE_BASE_DIR
import com.moira.wimb.global.utility.CommonVariables.MAIL_TEMPLATE_FILE_NAME_IDENTIFICATION
import com.moira.wimb.global.utility.CommonVariables.UTF_8
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

@Service
class MailAsyncSender(
    // utility
    private val apiResultLogger: ApiResultLogger,
    private val javaMailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine,

    // variables
    @Value($$"${spring.mail.username}")
    private val from: String
) {
    private val log = KotlinLogging.logger {}

    private fun getContext(event: MailEvent): Context {
        return Context().apply {
            when (event) {
                is IdentificationMailEvent -> {
                    setVariable("code", event.code)
                }
            }
        }
    }

    private fun getTemplateDir(event: MailEvent): String {
        return when (event) {
            is IdentificationMailEvent -> "${MAIL_TEMPLATE_BASE_DIR}/${MAIL_TEMPLATE_FILE_NAME_IDENTIFICATION}"
        }
    }

    private fun getTitle(event: MailEvent): String {
        return when (event) {
            is IdentificationMailEvent -> MAIL_SUBJECT_IDENTIFICATION
        }
    }

    private fun getSuccessLog(event: MailEvent): String {
        return when (event) {
            is IdentificationMailEvent -> {
                """
                
                [MailAsyncSender] 메일 전송 성공!
                - purpose: 본인인증
                - email: ${event.email}
                - identificationSeqNo: ${event.identificationSeqNo}
                - apiResultSeqNo: ${event.apiResultSeqNo}
                """.trimIndent()
            }
        }
    }

    private fun getFailLog(event: MailEvent, errMsg: String): String {
        return when (event) {
            is IdentificationMailEvent -> {
                """
                
                [MailAsyncSender] 메일 전송 실패!
                - purpose: 본인인증
                - email: ${event.email}
                - identificationSeqNo: ${event.identificationSeqNo}
                - apiResultSeqNo: ${event.apiResultSeqNo}
                - message: $errMsg
                """.trimIndent()
            }
        }
    }

    private fun executeMainLogic(event: MailEvent, isSuccess: Boolean) {
        when (event) {
            is IdentificationMailEvent -> {
                // TODO
            }
        }
    }

    /**
     * 메일 전송
     */
    @Async
    fun sendMail(event: MailEvent) {
        val email = event.email
        val apiResultSeqNo = event.apiResultSeqNo

        runCatching {
            // 1. MimeMessage 객체 생성
            val message = javaMailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, UTF_8)

            // 2. HTML 템플릿 생성
            val context = this.getContext(event)
            val htmlContent = templateEngine.process(this.getTemplateDir(event), context)

            // 3. 변수 설정
            helper.setFrom(from)
            helper.setTo(email)
            helper.setSubject(this.getTitle(event))
            helper.setText(htmlContent, true)

            // 4. 전송
            javaMailSender.send(message)
        }.onSuccess {
            log.info { this.getSuccessLog(event) }
            this.executeMainLogic(event, true)
            apiResultLogger.logSuccess(apiResultSeqNo)
        }.onFailure {
            log.error { this.getFailLog(event, it.message ?: "") }
            this.executeMainLogic(event, false)
            apiResultLogger.logFail(apiResultSeqNo, it.message ?: "")

            throw CommonException(ErrorCode.MAIL_SEND_FAILED)
        }
    }
}