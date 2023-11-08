package org.mbennett.authorization

import io.quarkus.security.AuthenticationFailedException
import io.quarkus.security.identity.AuthenticationRequestContext
import io.quarkus.security.identity.IdentityProvider
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.identity.request.TrustedAuthenticationRequest
import io.quarkus.security.runtime.QuarkusPrincipal
import io.quarkus.security.runtime.QuarkusSecurityIdentity
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.pgclient.PgPool
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.jboss.logging.Logger
import org.mbennett.repositories.UserRepository

@ApplicationScoped
class ApplicationTrustedIdentityProvider(private val userRepository: UserRepository): IdentityProvider<TrustedAuthenticationRequest> {
    private val LOG = Logger.getLogger(ApplicationTrustedIdentityProvider::class.java.toString())

    override fun getRequestType(): Class<TrustedAuthenticationRequest> =
        TrustedAuthenticationRequest::class.java

//    TODO: do a db lookup to verify user and their roles
    override fun authenticate(
        request: TrustedAuthenticationRequest,
        context: AuthenticationRequestContext?
    ): Uni<SecurityIdentity> =
        userRepository.getUser(request.principal.toLong())
            .onItem()
            .ifNull()
            .failWith(AuthenticationFailedException("no user"))
            .onItem()
            .transform{
                QuarkusSecurityIdentity
                    .builder()
                    .setPrincipal(
                        QuarkusPrincipal(it.id.toString())
                    )
                    .addAttributes(it.to())
                    .addRole("admin")
                    .build()
            }
}

