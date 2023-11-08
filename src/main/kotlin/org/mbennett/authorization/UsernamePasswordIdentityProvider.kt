package org.mbennett.authorization

import io.quarkus.security.AuthenticationFailedException
import io.quarkus.security.identity.AuthenticationRequestContext
import io.quarkus.security.identity.IdentityProvider
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest
import io.quarkus.security.runtime.QuarkusPrincipal
import io.quarkus.security.runtime.QuarkusSecurityIdentity
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import org.mbennett.repositories.UserRepository

@ApplicationScoped
class UsernamePasswordIdentityProvider(
    private val userRepository: UserRepository
) : IdentityProvider<UsernamePasswordAuthenticationRequest> {
    override fun getRequestType(): Class<UsernamePasswordAuthenticationRequest> {
        return UsernamePasswordAuthenticationRequest::class.java
    }

//    TODO: need to lookup password and hash password, then compare
    override fun authenticate(
        request: UsernamePasswordAuthenticationRequest?,
        context: AuthenticationRequestContext?
    ): Uni<SecurityIdentity> {
        val username = request?.username
        val password = request?.password
        if (username == null || password == null) {
            throw AuthenticationFailedException("username or password can not be blank")
        }

        return userRepository.findUserByEmail(username)
            .onItem()
            .ifNull()
            .failWith(AuthenticationFailedException("username or password invalid"))
            .onItem()
            .transform{
                QuarkusSecurityIdentity.builder()
                    .setPrincipal(QuarkusPrincipal(username))
                    .addCredential(password)
                    .addRole("admin")
                    .build()
        }
    }
}