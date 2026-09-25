package com.moira.wimb.global.auth

import com.moira.wimb.domain.category.mapper.PostCategoryMapper
import com.moira.wimb.global.exception.CommonException
import com.moira.wimb.global.exception.ErrorCode
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PathVariable

@Aspect
@Component
class ParameterAuthCheckAspect(
    private val postCategoryMapper: PostCategoryMapper
) {
    @Before("@annotation(com.moira.wimb.global.auth.ParameterAuthCheck)")
    fun checkMember(joinPoint: JoinPoint) {
        // 1. 파라미터 세팅
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val args = joinPoint.args
        val parameterAnnotations = method.parameterAnnotations

        var userId: String? = null
        var postCategoryId: Long? = null

        for (i in args.indices) {
            val annotations = parameterAnnotations[i]
            val parameterName = signature.parameterNames[i]

            val hasLoginUser = annotations.any { it.annotationClass.java == LoginUser::class.java }
            val hasPathVariable = annotations.any { it.annotationClass.java == PathVariable::class.java }

            // 2. userId 추출 (@LoginUser)
            if (hasLoginUser && parameterName == "userId") {
                userId = args[i] as? String
            }

            // 3. categoryId 추출 (@PathVariable) (필수값 x)
            if (hasPathVariable && parameterName == "postCategoryId") {
                postCategoryId = args[i] as? Long
            }
        }

        // 4. 유효성 검사
        if (
            userId == null
            ||
            (postCategoryId != null && !postCategoryMapper.selectIdAndUserIdChk(postCategoryId, userId))
        ) {
            throw CommonException(ErrorCode.FORBIDDEN)
        }
    }
}