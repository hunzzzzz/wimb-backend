package com.moira.wimb.global.utility

import com.moira.wimb.domain.infra.entity.FileIdentifier
import java.util.concurrent.ThreadLocalRandom

object CommonUtils {
    /**
     * 랜덤 ID 생성
     */
    fun createRandomId(identifier: String): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        val randomPart = (1..10)
            .map { allowedChars.random() }
            .joinToString("")

        return "$identifier$randomPart"
    }

    /**
     * Enum 값 검증
     */
    inline fun <reified T : Enum<T>> isValidEnum(value: String): Boolean {
        return runCatching { enumValueOf<T>(value) }.isSuccess
    }

    /**
     * AWS S3 Key값 추출
     */
    fun getS3Key(identifier: FileIdentifier, fileId: String, fileSeqNo: Int, originalFileName: String): String {
        return "${identifier.name}/${fileId}/${fileSeqNo}_${originalFileName}"
    }

    /**
     * 6자리 인증번호 생성
     */
    fun generateAuthCode(): String {
        val code = ThreadLocalRandom.current().nextInt(1000000)

        return code.toString().padStart(6, '0')
    }
}