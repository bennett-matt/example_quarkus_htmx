package org.mbennett.authorization

import io.netty.handler.codec.http.HttpResponseStatus
import io.quarkus.security.identity.IdentityProviderManager
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.identity.request.AuthenticationRequest
import io.quarkus.vertx.http.runtime.security.ChallengeData
import io.quarkus.vertx.http.runtime.security.FormAuthenticationMechanism
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism
import io.quarkus.vertx.http.runtime.security.QuarkusHttpUser
import io.smallrye.mutiny.Uni
import io.vertx.ext.web.RoutingContext
import jakarta.annotation.Priority
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Alternative
import jakarta.ws.rs.core.HttpHeaders
import jakarta.ws.rs.core.MediaType
import java.util.function.BiConsumer

@Alternative
@Priority(1)
@ApplicationScoped
class ApplicationAuthMechanism(private val delegate: FormAuthenticationMechanism) : HttpAuthenticationMechanism {
    override fun authenticate(
        context: RoutingContext?,
        identityProviderManager: IdentityProviderManager?
    ): Uni<SecurityIdentity> {
        context!!.data()[QuarkusHttpUser.AUTH_FAILURE_HANDLER] = customAuthErrorHandler()
        return delegate.authenticate(context, identityProviderManager)
    }

    override fun getChallenge(context: RoutingContext?): Uni<ChallengeData> {
        return delegate.getChallenge(context)
    }

    override fun getCredentialTypes(): MutableSet<Class<out AuthenticationRequest>> {
        return delegate.credentialTypes
    }

//    TODO: reimplement to return the corresponding response back for authentication failures
    private fun customAuthErrorHandler(): BiConsumer<RoutingContext, Throwable> {
        return BiConsumer { context, _ ->
            val json = "{\"taco\": \"man\"}"
            context.response().setStatusCode(HttpResponseStatus.UNAUTHORIZED.code())
                .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .putHeader(HttpHeaders.CONTENT_LENGTH, json.length.toString())
                .end(json)

        }
//        return (context, throwable) -> {
//            throwable = extractRootCause(throwable);
//            if (throwable instanceof AuthenticationFailedException) {
//                processFailedAuthentication((AuthenticationFailedException)throwable, context);
//            } else {
//                log.errorv(UNKNOWN_ERROR, throwable.getMessage());
//                String bodyResponse = ErrorDtoGenerator.generateErrorDto(
//                        HttpResponseStatus.UNAUTHORIZED.reasonPhrase(),
//                "Your token is not valid", "", Severity.ERROR.name());
//                context.response().setStatusCode(HttpResponseStatus.UNAUTHORIZED.code())
//                    .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
//                    .putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(bodyResponse.length()))
//                    .end(bodyResponse);
//            }
//        };
    }

    /**
     * <p>
     *   It will handle all possible authentication errors regarding the JWT validation.
     * </p>
     * */
//    private fun processFailedAuthentication(
//        authenticationError: AuthenticationFailedException,
//        context: RoutingContext
//    ) {
//        context.response()
//            .setStatusCode(HttpResponseStatus.UNAUTHORIZED.code())
//            .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
//        if (authenticationError.cause != null
//            && authenticationError.cause is ParseException
//        ) {
//            LOG.errorv(PARSING_JWT_ERROR, authenticationError.getCause().getCause().getMessage());
//            val errorBody = ErrorDtoGenerator.generateErrorDto(
//                HttpResponseStatus.UNAUTHORIZED.reasonPhrase(),
//                authenticationError.cause.message,
//                "",
//                SyslogHandler.Severity.ERROR.name
//            );
//            context.response()
//                .putHeader(HttpHeaders.CONTENT_LENGTH, (authenticationError.cause as ParseException).message!!.length)
//                .write(ParseException((authenticationError.cause as ParseException).message, 1) );
//        }
//        context.response().end();
//    }

//    private fun extractRootCause(throwable: Throwable ): Throwable {
//        while ((throwable instanceof CompletionException && throwable.getCause() != null)
//            || (throwable instanceof CompositeException)) {
//            if (throwable instanceof CompositeException) {
//                throwable = ((CompositeException) throwable).getCauses().get(0);
//            } else {
//                throwable = throwable.getCause();
//            }
//        }
//        return throwable;
//    }
}