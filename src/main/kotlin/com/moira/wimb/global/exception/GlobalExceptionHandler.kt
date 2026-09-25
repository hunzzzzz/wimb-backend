package com.moira.wimb.global.exception

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = KotlinLogging.logger {}

    private fun getErrorResponse(errorCode: ErrorCode): ErrorResponse {
        return ErrorResponse(errCode = errorCode.code, errName = errorCode.name, errMsg = errorCode.message)
    }

    /**
     * CommonException
     */
    @ExceptionHandler(CommonException::class)
    fun handleCommonException(e: CommonException): ResponseEntity<ErrorResponse> {
        val errorCode = e.errorCode

        // 1. 로깅
        log.error(e) { "🚨 [CommonException] 에러 발생 - (${errorCode.httpStatus}) [${errorCode.name}] ${e.message}" }

        // 2. 에러 응답값 생성 후 리턴
        return ResponseEntity.status(errorCode.httpStatus).body(this.getErrorResponse(errorCode))
    }

    /**
     * Exception
     */
    @ExceptionHandler(Exception::class)
    fun handleAllException(e: Exception): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.INTERNAL_SERVER_ERROR

        // 1. 로깅
        log.error(e) { "🚨 [Exception] 시스템 서버 에러 발생 - ${e.message}" }

        // 2. 에러 응답값 생성 후 리턴
        return ResponseEntity.status(errorCode.httpStatus).body(this.getErrorResponse(errorCode))
    }
}