package x.app.service.user.interceptor

import org.axonframework.modelling.command.Repository
import org.springframework.stereotype.Component
import x.app.service.user.User
import x.app.utils.extension.IExtensionExecutor
import x.app.utils.extension.interceptor.AbstractExtensionInterceptor

/**
 *   @Project: user
 *   @Package: x.app.service.user.interceptor
 *   @Author:  Iamee
 *   @Date:    2019-05-01 23:15
 */
@Component
class UserInterceptor(
        repository: Repository<User>,
        executor: IExtensionExecutor
) : AbstractExtensionInterceptor<User>(repository, executor)