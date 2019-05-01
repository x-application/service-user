package x.app.service.user.test.mock

import org.axonframework.commandhandling.CommandHandler
import x.app.common.AbstractResult
import x.app.common.account.command.ValidateAccountCommand

/**
 *   @Project: service-user
 *   @Package: x.app.service.user.test.mock
 *   @Author:  Iamee
 *   @Date:    2019-05-02 0:04
 */
class MockAccountCommandHandler {

    @CommandHandler
    fun handle(command: ValidateAccountCommand): AbstractResult {
        return ValidateAccountCommand.Result(userId = "userId", password = command.password)
    }

}