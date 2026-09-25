package com.moira.wimb.domain.user.controller

import com.moira.wimb.domain.user.dto.request.LoginRequest
import com.moira.wimb.domain.user.dto.request.SignupRequest
import com.moira.wimb.domain.user.dto.response.SimpleUserResponse
import com.moira.wimb.domain.user.service.UserService
import com.moira.wimb.global.auth.LoginUser
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService
) {
    /**
     * 회원가입
     */
    @PostMapping("/api/signup")
    fun signup(
        @RequestBody request: SignupRequest
    ): ResponseEntity<Nothing> {
        userService.signup(request)

        return ResponseEntity.status(HttpStatus.CREATED).body(null)
    }

    /**
     * 로그인
     */
    @PostMapping("/api/login")
    fun login(
        httpReq: HttpServletRequest,
        @RequestBody request: LoginRequest
    ): ResponseEntity<Nothing> {
        userService.login(request, httpReq)

        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

    /**
     * 로그아웃
     */
    @PostMapping("/api/logout")
    fun logout(httpReq: HttpServletRequest): ResponseEntity<Nothing> {
        userService.logout(httpReq)

        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

    /**
     * 내 정보 조회
     */
    @GetMapping("/api/me")
    fun me(@LoginUser userId: String): ResponseEntity<SimpleUserResponse> {
        val response = userService.me(userId)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}