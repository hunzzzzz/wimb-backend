package com.moira.wimb.domain.infra.mapper

import com.moira.wimb.domain.infra.entity.ApiResult
import org.apache.ibatis.annotations.Mapper

@Mapper
interface ApiResultMapper {
    fun selectApiMailResultInOneMinuteChk(email: String): Boolean

    fun insertApiResult(apiResult: ApiResult): Long

    fun updateStatus(seqNo: Long, status: String)
    fun updateStatusAndFailMessage(seqNo: Long, status: String, failMessage: String)
}