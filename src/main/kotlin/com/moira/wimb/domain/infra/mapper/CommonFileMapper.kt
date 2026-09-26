package com.moira.wimb.domain.infra.mapper

import com.moira.wimb.domain.infra.entity.CommonFile
import org.apache.ibatis.annotations.Mapper

@Mapper
interface CommonFileMapper {
    fun selectAllForDeleteScheduler(): List<CommonFile>

    fun selectCommonFile(
        fileId: String,
        fileSeqNo: Int,
        userId: String
    ): CommonFile?

    fun selectCommonFile2(
        fileId: String,
        fileSeqNo: Int,
        userId: String,
        identifier: String,
        status: String
    ): CommonFile?

    fun insertCommonFileBulk(files: List<CommonFile>)

    fun updateStatusUploaded(fileId: String, fileSeqNo: Int, userId: String)

    fun updateStatusFailed(fileId: String, fileSeqNo: Int, userId: String)

    fun updateStatusByFileId(fileId: String, userId: String, status: String)

    fun deleteCommonFile(fileId: String, fileSeqNo: Int)
}