package com.moira.wimb.global.filter

import com.moira.wimb.global.exception.ErrorCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"

        val errorCode = ErrorCode.EXPIRED_USER_INFO

        response.writer.write(
            """
                {
                    "errCode": "${errorCode.code}",
                    "errName": "${errorCode.name}",
                    "errMsg": "${errorCode.message}",
                    "time": "${LocalDateTime.now()}"
                }
            """.trimIndent()
        )
    }
}