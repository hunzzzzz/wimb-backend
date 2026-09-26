package com.moira.wimb.domain.infra.mapper

import com.moira.wimb.domain.infra.entity.CommonFile
import org.apache.ibatis.annotations.Mapper

@Mapper
interface CommonFileMapper {
    fun selectStatus(fileId: String, fileSeqNo: Int, userId: String): String?
    fun selectAllForDeleteScheduler(): List<CommonFile>
    fun selectIdAndUserIdChk(fileId: String, userId: String): Boolean
    fun selectCommonFile(
        fileId: String,
        fileSeqNo: Int,
        userId: String,
        identifier: String,
        status: String
    ): CommonFile?

    fun insertCommonFileBulk(files: List<CommonFile>)

    fun updateStatus(
        fileId: String,
        fileSeqNo: Int,
        userId: String,
        status: String
    )

    fun updateStatusAndUploadedAt(
        fileId: String,
        fileSeqNo: Int,
        userId: String,
        status: String
    )

    fun updateStatusByFileId(
        fileId: String,
        userId: String,
        status: String,
    )

    fun delete(fileId: String, fileSeqNo: Int)
}