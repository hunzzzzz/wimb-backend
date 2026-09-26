package com.moira.wimb.domain.infra.mapper

import com.moira.wimb.domain.infra.entity.Identification
import org.apache.ibatis.annotations.Mapper
import java.time.LocalDateTime

@Mapper
interface IdentificationMapper {
    fun selectRecentIdentification(email: String, purpose: String): Identification?

    fun insertIdentification(identification: Identification): Long

    fun updateStatus(seqNo: Long, status: String)
    fun updateStatusAndExpiresAt(seqNo: Long, status: String, expiresAt: LocalDateTime)
    fun updateFailCountIncrement(identificationSeqNo: Long)
}