package org.mbennett.authorization

import io.quarkus.elytron.security.common.BcryptUtil
import io.quarkus.security.AuthenticationFailedException
import io.quarkus.security.credential.PasswordCredential
import io.quarkus.security.identity.AuthenticationRequestContext
import io.quarkus.security.identity.IdentityProvider
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest
import io.quarkus.security.runtime.QuarkusPrincipal
import io.quarkus.security.runtime.QuarkusSecurityIdentity
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import org.mbennett.models.User
import org.mbennett.repositories.UserRepository

@ApplicationScoped
class UsernamePasswordIdentityProvider(
    private val userRepository: UserRepository
) : IdentityProvider<UsernamePasswordAuthenticationRequest> {
    override fun getRequestType(): Class<UsernamePasswordAuthenticationRequest> {
        return UsernamePasswordAuthenticationRequest::class.java
    }

    override fun authenticate(
        request: UsernamePasswordAuthenticationRequest?,
        context: AuthenticationRequestContext?
    ): Uni<SecurityIdentity> {
        val username = request?.username
        val credential = request?.password
        if (username == null || credential == null) {
            throw AuthenticationFailedException("username or password can not be blank")
        }

        return userRepository.findUserByEmail(username)
            .onItem()
            .ifNull()
            .failWith(AuthenticationFailedException("username or password invalid"))
            .onItem()
            .transform {checkPassword(String(credential.password), it)}
            .onItem()
            .ifNull()
            .failWith(AuthenticationFailedException("username or password invalid"))
            .onItem()
            .transform(this::createSecurityIdentity)
    }

    private fun checkPassword(password: String, user: User): User? {
        if (BcryptUtil.matches(password, user.hashedPassword))
            return user

        return null
    }

    private fun createSecurityIdentity(user: User?): SecurityIdentity =
        QuarkusSecurityIdentity.builder()
            .setPrincipal(QuarkusPrincipal(user!!.id.toString()))
            .addAttributes(user.to())
            .addRole("admin")
            .build()
}