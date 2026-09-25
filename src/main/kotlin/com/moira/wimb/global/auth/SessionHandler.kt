package com.moira.wimb.global.auth

import com.moira.wimb.domain.user.entity.User
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.stereotype.Component

@Component
class SessionHandler {
    /**
     * 인증 정보 설정 + 세션 세팅
     */
    fun setAuth(user: User, httpReq: HttpServletRequest) {
        // 1. 권한 생성
        val authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role}"))

        // 2. Authentication 객체 생성
        val authentication = UsernamePasswordAuthenticationToken(user.userId, null, authorities)

        // 3. SecurityContextHolder에 인증 정보 설정
        val context = SecurityContextHolder.getContext()
        context.authentication = authentication

        // 4. 세션 확보 (없으면 생성)
        val session = httpReq.getSession(true)

        // 5. 세션 고정 공격 방지: 세션 ID를 새로 발급 (session은 유지, ID만 교체)
        httpReq.changeSessionId()

        // 6. HttpSession에 SecurityContext 등록
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context)
    }

    /**
     * 인증 정보 제거 + 세션 만료
     */
    fun deleteAuth(httpReq: HttpServletRequest) {
        // 1. 현재 Thread의 SecurityContext 비우기
        SecurityContextHolder.clearContext()

        // 2. 현재 세션이 존재한다면 invalidate
        val session = httpReq.getSession(false)
        session?.invalidate()
    }
}