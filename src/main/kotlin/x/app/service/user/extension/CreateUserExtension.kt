package x.app.service.user.extension

import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.modelling.command.Repository
import x.app.common.account.command.BindAccountCommand
import x.app.common.account.command.CreateAccountCommand
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
        BindAccountCommand(accountId = command.accountId, accountType = command.accountType, userId = command.userId) sendTo commandGateway
    }

}