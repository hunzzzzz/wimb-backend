package com.moira.wimb.global.exception

class CommonException(val errorCode: ErrorCode) : RuntimeException(errorCode.message)