package com.moira.wimb.domain.infra.entity

enum class FileStatus {
    PENDING, UPLOADED, USING, // 정로직
    DELETED, FAILED // 예외
}