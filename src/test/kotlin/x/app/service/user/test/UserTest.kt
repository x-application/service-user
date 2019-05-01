package x.app.service.user.test

import at.favre.lib.crypto.bcrypt.BCrypt
import at.favre.lib.crypto.bcrypt.LongPasswordStrategy
import org.axonframework.commandhandling.gateway.DefaultCommandGateway
import org.axonframework.common.IdentifierFactory
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.Before
import org.junit.Test
import x.app.common.CommonService
import x.app.common.user.command.LoginUserCommand
import x.app.common.user.event.UserCreatedEvent
import x.app.service.user.User
import x.app.service.user.extension.LoginUserExtension
import x.app.service.user.handler.UserCommandHandler
import x.app.service.user.interceptor.UserInterceptor
import x.app.service.user.test.mock.MockAccountCommandHandler
import x.app.utils.extension.IExtensionExecutor
import java.security.SecureRandom
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
        executor.add(LoginUserExtension(commandGateway = DefaultCommandGateway.builder().commandBus(fixture.commandBus).build()))
        fixture.registerAnnotatedCommandHandler(UserCommandHandler(repository = fixture.repository, service = commandService))
        fixture.registerAnnotatedCommandHandler(MockAccountCommandHandler())
        fixture.registerCommandHandlerInterceptor(UserInterceptor(repository = fixture.repository, executor = executor))
    }

    @Test
    fun login() {
        val password = "password"
        fixture
                .given(UserCreatedEvent(userId = userId, password = password, time = commandService.currentTimeMillis()))
                .`when`(LoginUserCommand(userId = "", accountId = "foo", password = "password"))
                .expectSuccessfulHandlerExecution()
    }

    @Test
    fun bcrypt() {
        val bCrypt = BCrypt.with(BCrypt.Version.VERSION_2B)

        val s = SecureRandom(userId.toByteArray())

        val password = "password"

        for (i in 0..100){
            val result = bCrypt.hash(8, s.generateSeed(16), password.toByteArray())
            println(String(result))
            println(String(bCrypt.hash(8, password.toByteArray())))
        }

//        println(BCrypt.verifyer().verifyStrict(password.toByteArray(), result, BCrypt.Version.VERSION_2B))
    }

}