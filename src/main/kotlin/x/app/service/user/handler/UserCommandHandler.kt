package x.app.service.user.handler

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.modelling.command.Repository
import x.app.common.AbstractResult
import x.app.common.CommonService
import x.app.common.user.command.CreateUserCommand
import x.app.common.user.command.LoginUserCommand
import x.app.service.user.User

/**
 *   @Project: user
 *   @Package: x.app.service.user.handler
 *   @Author:  Iamee
 *   @Date:    2019-05-01 4:35
 */
class UserCommandHandler(
        val repository: Repository<User>,
        val service: CommonService
) {

    @CommandHandler
    fun handle(command: CreateUserCommand): AbstractResult {
        repository.newInstance {
            User(userId = command.userId, password = command.password, time = service.currentTimeMillis())
        }.invoke { it }.run { return CreateUserCommand.Result(userId = userId) }
    }

    @CommandHandler
    fun handle(command: LoginUserCommand): AbstractResult {
        repository.load(command.getIdentifier()).invoke {
            it.login(password = command.password, time = service.currentTimeMillis())
            LoginUserCommand.Result(userId = it.userId)
        }.run { return this }
    }

}