package x.app.service.user

import at.favre.lib.crypto.bcrypt.BCrypt
import at.favre.lib.crypto.bcrypt.BCrypt.MAX_PW_LENGTH_BYTE
import at.favre.lib.crypto.bcrypt.LongPasswordStrategy
import org.axonframework.eventhandling.EventHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate
import x.app.common.user.event.UserCreatedEvent
import x.app.common.user.event.UserLoggedInEvent
import x.app.common.user.exception.UserValidateFailedException
import java.lang.Exception
import java.security.SecureRandom

/**
 *   @Project: user
 *   @Package: x.app.service.user
 *   @Author:  Iamee
 *   @Date:    2019-05-01 4:34
 */
@Aggregate
class User {

    @AggregateIdentifier
    lateinit var id: String

    @AggregateMember
    lateinit var userId: String

    @AggregateMember
    lateinit var password: String

    @AggregateMember
    lateinit var accounts: HashSet<String>

    constructor()

    constructor(userId: String, password: String, time: Long) {
        AggregateLifecycle.apply(UserCreatedEvent(userId = userId, password = password, time = time))
    }

    @EventHandler
    fun on(event: UserCreatedEvent) {
        this.id = event.getIdentifier()
        this.userId = event.userId
        this.password = BCrypt.with(BCrypt.Version.VERSION_2B, SecureRandom(this.userId.toByteArray())) {
            it
        }.hashToString(8, event.password.toCharArray())
        this.accounts = HashSet()
    }

    fun login(password: String, time: Long) {
        BCrypt.verifyer().verifyStrict(password.toByteArray(), this.password.toByteArray(), BCrypt.Version.VERSION_2B).run {
            if (this.verified) {
                AggregateLifecycle.apply(UserLoggedInEvent(userId = userId, time = time))
            } else {
                throw UserValidateFailedException(userId = userId)
            }
        }
    }

}