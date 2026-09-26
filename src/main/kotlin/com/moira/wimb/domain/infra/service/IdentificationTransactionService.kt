package com.moira.wimb.domain.infra.service

import com.moira.wimb.domain.infra.entity.IdentificationStatus
import com.moira.wimb.domain.infra.mapper.IdentificationMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class IdentificationTransactionService(
    // mapper
    private val identificationMapper: IdentificationMapper
) {
    /**
     * 상태값 업데이트
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun updateStatus(identificationSeqNo: Long, status: IdentificationStatus) {
        identificationMapper.updateStatus(
            seqNo = identificationSeqNo,
            status = status.name
        )
    }

    /**
     * failCount + 1
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun incrementFailCount(identificationSeqNo: Long) {
        // 1. Identification의 failCount를 +1
        identificationMapper.updateFailCountIncrement(identificationSeqNo)
    }
}