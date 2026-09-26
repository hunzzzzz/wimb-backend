package com.moira.wimb.domain.infra.entity

import java.time.LocalDateTime

class CommonFile(
    var fileId: String, // 파일 ID (1개 이상의 파일의 묶음)
    var fileSeqNo: Int, // 하나의 파일 ID 하위의 개별 파일을 구분할 수 있는 번호 (1부터 시작)
    var userId: String,
    var status: String = FileStatus.PENDING.name,
    var fileUrl: String,
    var originalFileName: String,
    var size: Long,
    var contentType: String,
    var identifier: String,
    var s3Key: String,
    var representYn: String, // 대표이미지 여부 (Y/N)
    var createdAt: LocalDateTime = LocalDateTime.now(), // status가 PENDING이 되는 시점 (객체 생성 시)
    var uploadedAt: LocalDateTime? = null, // status가 UPLOAD_SUCCESS가 되는 시점
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)