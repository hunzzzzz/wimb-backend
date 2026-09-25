package com.moira.wimb.global.filter

import com.moira.wimb.global.utility.CommonVariables.UNKNOWN
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
class LoggingFilter : OncePerRequestFilter() {
    private val log = KotlinLogging.logger {}

    /**
     * 로깅
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 1. HttpServletRequest, HttpServletResponse를 Wrapping
        // HttpServletRequest의 body(InputStream)은 한번 읽으면 다시 못읽음 -> 컨트롤러 단에서 @RequestBody로 파싱 불가
        // 따라서 ContentCachingRequestWrapper/ContentCachingResponseWrapper 사용
        val wrappedRequest = ContentCachingRequestWrapper(request, 10 * 1024) // 10KB
        val wrappedResponse = ContentCachingResponseWrapper(response)
        val startTime = System.currentTimeMillis()

        // 2. 메인 로직 실행
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse)
        } finally {
            // 3. 로깅
            val userId = SecurityContextHolder.getContext().authentication?.principal ?: UNKNOWN
            val requestBody = String(wrappedRequest.contentAsByteArray, Charsets.UTF_8)
                .replace(Regex("\\s+"), " ")
                .trim() // requestBody의 줄바꿈/들여쓰기를 한 줄로 압축 (trimIndent가 body의 개행에 영향받지 않도록)
            val duration = System.currentTimeMillis() - startTime

            log.info {
                """
                
                ========== 접속 정보 ==========
                [${wrappedRequest.method}] ${wrappedRequest.requestURI} ${wrappedRequest.queryString?.let { "?$it" } ?: ""}
                - userId: $userId
                - requestBody: $requestBody
                - status: ${wrappedResponse.status}
                - duration: ${duration}ms
                """.trimIndent()
            }

            // 4. 응답 body를 copy
            wrappedResponse.copyBodyToResponse()
        }
    }
}