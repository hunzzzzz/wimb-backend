package com.moira.wimb.global.auth

import com.moira.wimb.global.exception.CommonException
import com.moira.wimb.global.exception.ErrorCode
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class LoginUserArgumentResolver : HandlerMethodArgumentResolver {
    /**
     * 어떤 파라미터에 Resolver를 적용할지 조건 설정
     */
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        val hasAnnotation = parameter.hasParameterAnnotation(LoginUser::class.java)
        val isStringType = parameter.parameterType == String::class.java

        return hasAnnotation && isStringType
    }

    /**
     * supportsParameter가 true를 반환했을 때, 실제로 파라미터에 넣어줄 값을 세팅
     */
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): String? {
        // 1. Authentication 객체 획득
        val authentication = SecurityContextHolder.getContext().authentication

        // 2. 인증 정보가 없거나 비로그인 유저(anonymousUser)인 경우 null 또는 예외 처리
        if (authentication == null || !authentication.isAuthenticated || authentication.principal == "anonymousUser") {
            throw CommonException(ErrorCode.EXPIRED_USER_INFO)
        }

        // 3. userId(authentication 객체의 principal에 저장됨) 리턴
        return authentication.principal as? String
    }
}