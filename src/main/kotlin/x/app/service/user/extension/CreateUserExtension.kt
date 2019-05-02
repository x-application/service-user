package x.app.service.user.extension

import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.modelling.command.Repository
import x.app.common.account.command.CreateAccountBondCommand
import x.app.common.account.command.CreateAccountCommand
import x.app.common.account.exception.AccountAlreadExistsException
import x.app.common.dsl.sendTo
import x.app.common.user.command.CreateUserCommand
import x.app.service.user.User
import x.app.utils.extension.annotation.Extension
import x.app.utils.extension.point.ICommandExtensionPoint

/**
 *   @Project: user
 *   @Package: x.app.service.user.extension
 *   @Author:  Iamee
 *   @Date:    2019-05-01 23:20
 */
@Extension(CreateUserCommand::class)
class CreateUserExtension(
        private val commandGateway: CommandGateway
) : ICommandExtensionPoint<CreateUserCommand, User> {

    override fun before(repository: Repository<User>, command: CreateUserCommand) {
        try {
            CreateAccountCommand(accountType = command.accountType, accountId = command.accountId) sendTo commandGateway
        } catch (ex: AccountAlreadExistsException) {
            //如果账户已存在过滤不抛出异常
        } catch (ex: Throwable) {
            throw ex
        } finally {
            CreateAccountBondCommand(accountId = command.accountId, userId = command.userId, accountType = command.accountType) sendTo commandGateway
        }
    }

}