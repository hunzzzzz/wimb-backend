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
        code = "COM03",
        httpStatus = HttpStatus.FORBIDDEN,
        message = "비정상적인 접근입니다."
    ),

    // 사용자
    LOGIN_ID_EXISTS(
        code = "USR01",
        httpStatus = HttpStatus.BAD_REQUEST,
        message = "이미 사용 중인 ID입니다."
    ),
    EMAIL_EXISTS(
        code = "USR02",
        httpStatus = HttpStatus.BAD_REQUEST,
        message = "이미 사용 중인 이메일입니다."
    ),
    LOGIN_FAILED(
        code = "USR03",
        httpStatus = HttpStatus.BAD_REQUEST,
        message = "ID 혹은 비밀번호가 일치하지 않습니다."
    ),
    USER_NOT_FOUND(
        code = "USR04",
        httpStatus = HttpStatus.FORBIDDEN,
        message = "존재하지 않는 사용자입니다."
    ),
    BANNED_USER_CANNOT_LOGIN(
        code = "USR05",
        httpStatus = HttpStatus.BAD_REQUEST,
        message = "계정이 정지된 사용자입니다."
    ),
    DELETED_USER_CANNOT_LOGIN(
        code = "USR06",
        httpStatus = HttpStatus.BAD_REQUEST,
        message = "회원탈퇴한 계정입니다."
    ),

    // 게시글
    CATEGORY_NAME_EXISTS(
        code = "PST01",
        httpStatus = HttpStatus.BAD_REQUEST,
        message = "이미 사용 중인 카테고리명입니다."
    ),
    CATEGORY_COUNT_EXCEEDED(
        code = "PST02",
        httpStatus = HttpStatus.BAD_REQUEST,
        message = "카테고리는 최대 10개까지 등록이 가능합니다."
    ),
    INVALID_CATEGORY_ORDER(
        code = "PST03",
        httpStatus = HttpStatus.BAD_REQUEST,
        message = "카테고리 순서 변경 요청 정보가 올바르지 않습니다. 목록을 새로고침 후 다시 시도해 주세요."
    ),
    CANNOT_DELETE_USING_CATEGORY(
        code = "PST04",
        httpStatus = HttpStatus.BAD_REQUEST,
        message = "게시글이 존재하는 카테고리는 삭제할 수 없습니다."
    ),

    // 시스템
    PASSWORD_ENCRYPTION_FAILED(
        code = "SYS01",
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        message = "비밀번호 암호화에 실패하였습니다."
    ),
}