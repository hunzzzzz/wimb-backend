package com.moira.wimb.global.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val code: String,
    val httpStatus: HttpStatus,
    val message: String
) {
    // 공통
    INTERNAL_SERVER_ERROR(
        code = "COM01",
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        message = "알 수 없는 오류가 발생했습니다. 해당 오류가 반복되는 경우 고객센터로 문의해주세요."
    ),
    EXPIRED_USER_INFO(
        code = "COM02",
        httpStatus = HttpStatus.UNAUTHORIZED,
        message = "유저 정보가 만료되었습니다. 다시 로그인해주세요."
    ),
    FORBIDDEN(
        code = "CM03",
        httpStatus = HttpStatus.FORBIDDEN,
        message = "비정상적인 접근입니다."
    )
}