package com.moira.wimb.global.utility

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
}