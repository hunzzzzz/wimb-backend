package com.moira.wimb.global.mail

import com.moira.wimb.global.mail.event.MailEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

/**
 * 이벤트 리스너
 * -> @TransactionalEventListener는 내부적으로 AOP 프록시를 통해 현재 진행 중인 트랜잭션의 커밋/롤백에 리스너를 등록
 * -> 즉, 각 메인로직 서비스 코드의 트랜잭션이 100% 커밋된 후에, 메일 전송 로직이 호출되는 것을 보장
 */
@Component
class MailEventListener(
    private val mailAsyncSender: MailAsyncSender
) {
    /**
     * 메일 전송 이벤트 리스너
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun sendMail(event: MailEvent) {
        mailAsyncSender.sendMail(event)
    }
}