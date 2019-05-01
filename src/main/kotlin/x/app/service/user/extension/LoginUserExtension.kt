package x.app.service.user.extension

import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.modelling.command.Repository
import x.app.common.AbstractCommand
import x.app.common.AbstractResult
import x.app.common.account.command.ValidateAccountCommand
import x.app.common.user.command.LoginUserCommand
import x.app.service.user.User
import x.app.utils.extension.annotation.Extension
import x.app.utils.extension.point.ICommandExtensionPoint

/**
 *   @Project: user
 *   @Package: x.app.service.user.extension
 *   @Author:  Iamee
 *   @Date:    2019-05-01 23:28
 */
@Extension(LoginUserCommand::class)
class LoginUserExtension(
        private val commandGateway: CommandGateway
) : ICommandExtensionPoint<LoginUserCommand, User> {

    override fun before(repository: Repository<User>, command: LoginUserCommand) {
        val result = (ValidateAccountCommand(accountId = command.accountId, password = command.password) sendTo commandGateway)
        command.userId = result.userId
        command.password = result.password
    }

}

@Suppress("UNCHECKED_CAST")
infix fun <T : AbstractResult> AbstractCommand<T>.sendTo(commandGateway: CommandGateway): T = commandGateway.sendAndWait<AbstractResult>(this).run {
    this.exception?.run { throw this }
    this as T
}