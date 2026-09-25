package com.moira.wimb.global.config

import com.moira.wimb.global.filter.CustomAuthenticationEntryPoint
import com.moira.wimb.global.filter.LoggingFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val loggingFilter: LoggingFilter
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.getAuthenticationManager()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf(
                "http://localhost:3000",
                "https://whats-in-my-brain-qa.lovable.app"
            )
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)

        return source
    }

    @Bean
    fun filterChain(http: HttpSecurity, corsConfigurationSource: CorsConfigurationSource): SecurityFilterChain {
        http {
            // 1. CSRF, 세션, Form 로그인 관련 기능 비활성화
            csrf { disable() }
            formLogin { disable() }
            httpBasic { disable() }

            // 2. 세션 기반 로그인
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.IF_REQUIRED }

            // 3. CORS 처리
            cors { configurationSource = corsConfigurationSource }

            // 4. 예외 처리 (인증되지 않은 요청 시 401 에러 반환)
            exceptionHandling {
                authenticationEntryPoint = customAuthenticationEntryPoint
            }

            // 5. 인증/인가 규칙 설정
            authorizeHttpRequests {
                // 회원가입
                authorize(HttpMethod.POST, "/api/signup/**", permitAll)
                // 로그인
                authorize(HttpMethod.POST, "/api/login/**", permitAll)
                authorize(HttpMethod.POST, "/api/posts/**", permitAll)
                // 나머지 요청은 인증 필수
                authorize(anyRequest, authenticated)
            }

            // 6. 필터 등록
            addFilterBefore<UsernamePasswordAuthenticationFilter>(loggingFilter)
        }

        return http.build()
    }
}