package com.moira.wimb.domain.infra.controller

import com.moira.wimb.domain.infra.dto.request.IdentificationCodeConfirmRequest
import com.moira.wimb.domain.infra.dto.request.IdentificationMailSendRequest
import com.moira.wimb.domain.infra.service.IdentificationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class IdentificationController(
    private val identificationService: IdentificationService
) {
    /**
     * 인증번호 메일 전송
     */
    @PostMapping("/api/identification/mail")
    fun sendCode(
        @RequestBody request: IdentificationMailSendRequest
    ): ResponseEntity<Nothing> {
        identificationService.sendCode(request)

        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

    /**
     * 인증번호 확인
     */
    @PostMapping("/api/identification/mail/confirm")
    fun confirmCode(
        @RequestBody request: IdentificationCodeConfirmRequest
    ): ResponseEntity<Nothing> {
        identificationService.confirmCode(request)

        return ResponseEntity.status(HttpStatus.OK).body(null)
    }
}