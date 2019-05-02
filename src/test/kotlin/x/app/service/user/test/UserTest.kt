package x.app.service.user.test

import org.axonframework.commandhandling.gateway.DefaultCommandGateway
import org.axonframework.common.IdentifierFactory
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.Before
import org.junit.Test
import x.app.common.CommonService
import x.app.common.account.exception.AccountAlreadyBondException
import x.app.common.user.command.CreateUserCommand
import x.app.common.user.event.UserCreatedEvent
import x.app.service.user.User
import x.app.service.user.extension.CreateUserExtension
import x.app.service.user.extension.LoginUserExtension
import x.app.service.user.handler.UserCommandHandler
import x.app.service.user.interceptor.UserInterceptor
import x.app.service.user.test.mock.MockAccountCommandHandler
import x.app.utils.extension.IExtensionExecutor
import kotlin.reflect.KClass

/**
 *   @Project: service-user
 *   @Package: x.app.service.user.test
 *   @Author:  Iamee
 *   @Date:    2019-05-01 23:55
 */
class UserTest {

    lateinit var fixture: AggregateTestFixture<User>

    lateinit var commandService: CommonService

    lateinit var executor: IExtensionExecutor

    private val userId: String = "userId"

    @Before
    fun setup() {
        fixture = AggregateTestFixture(User::class.java)
        val commandGateway = DefaultCommandGateway.builder().commandBus(fixture.commandBus).build()
        commandService = object : CommonService {
            override fun currentTimeMillis(): Long {
                return 0L
            }

            override fun generateIdentifier(): String {
                return IdentifierFactory.getInstance().generateIdentifier()
            }
        }
        executor = object : IExtensionExecutor {
            override val maps: HashMap<KClass<*>, Any> = HashMap()
        }
        executor.add(LoginUserExtension(commandGateway = commandGateway))
        executor.add(CreateUserExtension(commandGateway = commandGateway))
        fixture.registerAnnotatedCommandHandler(UserCommandHandler(repository = fixture.repository, service = commandService))
        fixture.registerAnnotatedCommandHandler(MockAccountCommandHandler())
        fixture.registerCommandHandlerInterceptor(UserInterceptor(repository = fixture.repository, executor = executor))
    }

    @Test
    fun create() {
        val accountId = "10001"
        val accountType = "ac-type"
        val password = "password"
        fixture
                .givenNoPriorActivity()
                .`when`(CreateUserCommand(userId = userId, accountId = accountId, accountType = accountType, password = password))
                .expectSuccessfulHandlerExecution()
                .expectEvents(UserCreatedEvent(userId = userId, password = password, time = commandService.currentTimeMillis()))
    }

    @Test
    fun createWithAccountAlreadyBondException() {
        val accountId = "10000"
        val accountType = "ac-type"
        val password = "password"
        fixture
                .givenNoPriorActivity()
                .`when`(CreateUserCommand(userId = userId, accountId = accountId, accountType = accountType, password = password))
                .expectException(AccountAlreadyBondException::class.java)
    }
}