package x.app.service.user.test.mock

import org.axonframework.commandhandling.CommandHandler
import x.app.common.AbstractResult
import x.app.common.account.command.CreateAccountBondCommand
import x.app.common.account.command.CreateAccountCommand
import x.app.common.account.command.ValidateAccountCommand
import x.app.common.account.exception.AccountAlreadExistsException
import x.app.common.account.exception.AccountAlreadyBondException

/**
 *   @Project: service-user
 *   @Package: x.app.service.user.test.mock
 *   @Author:  Iamee
 *   @Date:    2019-05-02 0:04
 */
class MockAccountCommandHandler {

    @CommandHandler
    fun handle(command: CreateAccountCommand): AbstractResult {
        if (command.accountId == "10000") throw AccountAlreadExistsException(accountId = command.accountId)
        return CreateAccountCommand.Result(accountId = command.accountId)
    }

    @CommandHandler
    fun handle(command: CreateAccountBondCommand): AbstractResult {
        if (command.accountId == "10000") throw AccountAlreadyBondException(accountId = command.accountId)
        return CreateAccountBondCommand.Result(accountId = command.accountId, userId = command.userId)
    }

    @CommandHandler
    fun handle(command: ValidateAccountCommand): AbstractResult {
        return ValidateAccountCommand.Result(userId = "userId", password = command.password)
    }

}