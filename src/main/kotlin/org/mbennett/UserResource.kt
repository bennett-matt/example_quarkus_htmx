package org.mbennett

import io.quarkus.qute.CheckedTemplate
import io.quarkus.qute.TemplateInstance
import jakarta.transaction.Transactional
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import java.util.logging.Logger

@Path("/users")
class UserResource {
    private val LOG = Logger.getLogger(UserResource::class.java.toString())

    @CheckedTemplate
    class Templates {
        companion object {
            @JvmStatic
            external fun auth(signUp: Boolean): TemplateInstance
        }
    }

    @GET
    @Path("login")
    @Produces(MediaType.TEXT_HTML)
    suspend fun login(): TemplateInstance {
        return Templates.auth(false)
    }

    @GET
    @Path("sign-up")
    @Produces(MediaType.TEXT_HTML)
    suspend fun signUp(): TemplateInstance {
        return Templates.auth(true)
    }

    @POST
    @Path("loginPost")
    @Transactional
    fun loginPost(@FormParam("email") email: String, @FormParam("password") password: String) {
        LOG.info("Email: $email, Password: $password")
    }

    @POST
    @Path("sign-up")
    suspend fun signUpPost(@FormParam("name") name: String, @FormParam("email") email: String, @FormParam("password") password: String, @FormParam("confirmPassword") confirmPassword: String) {
        TODO("implement me")
    }
}
