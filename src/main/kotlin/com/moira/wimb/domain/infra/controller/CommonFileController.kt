package com.moira.wimb.domain.infra.controller

import com.moira.wimb.domain.infra.dto.request.FileUploadResultRequest
import com.moira.wimb.domain.infra.dto.request.PresignedUrlRequest
import com.moira.wimb.domain.infra.dto.response.PresignedUrlResponse
import com.moira.wimb.domain.infra.service.CommonFileService
import com.moira.wimb.global.auth.LoginUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CommonFileController(
    private val commonFileService: CommonFileService
) {
    /**
     * 파일 업로드 > Presigned Url 발급
     */
    @PostMapping("/api/files/upload/presigned-url")
    fun presignedUrl(
        @LoginUser userId: String,
        @RequestBody request: PresignedUrlRequest
    ): ResponseEntity<PresignedUrlResponse> {
        val response = commonFileService.presignedUrl(userId, request)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    /**
     * 파일 업로드 > 업로드 결과 전송
     */
    @PostMapping("/api/files/upload/result")
    fun uploadResult(
        @LoginUser userId: String,
        @RequestBody request: FileUploadResultRequest
    ): ResponseEntity<Nothing> {
        commonFileService.uploadResult(userId, request)

        return ResponseEntity.status(HttpStatus.OK).body(null)
    }
}