package org.mbennett.authorization

import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.runtime.QuarkusSecurityIdentity
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Named
import jakarta.inject.Singleton
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.SecurityContext
import org.jboss.logging.Logger
import org.mbennett.models.User
import org.mbennett.repositories.UserRepository
import java.security.Principal

@Named("authCtx")
@RequestScoped
class AuthenticationContext(
    private val securityContext: SecurityContext,
    private val securityIdentity: SecurityIdentity,
    private val userRepository: UserRepository
) {
    private val LOG = Logger.getLogger(AuthenticationContext::class.java.toString())

    fun getCurrentUser(): User? {
        LOG.info(securityIdentity.attributes)
        if (securityIdentity.principal != null) {
            return User.from(securityIdentity.attributes)
        }

        return null
    }
}