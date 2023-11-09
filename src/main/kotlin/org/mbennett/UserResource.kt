package org.mbennett

import io.quarkus.qute.CheckedTemplate
import io.quarkus.qute.TemplateInstance
import io.smallrye.mutiny.Uni
import jakarta.transaction.Transactional
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.UriInfo
import org.mbennett.models.UserAuthForm
import org.mbennett.repositories.UserRepository
import java.util.logging.Logger

@Path("/users")
class UserResource(private val userRepository: UserRepository) {
    private val LOG = Logger.getLogger(UserResource::class.java.toString())

    @CheckedTemplate
    class Templates {
        companion object {
            @JvmStatic
            external fun auth(signUp: Boolean): TemplateInstance
            @JvmStatic
            external fun form(form: UserAuthForm): TemplateInstance
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

//    TODO: also want to implement flash messages on success/failures for user
    @POST
    @Path("sign-up")
    @Transactional
    fun signUpPost(
        @FormParam("name") name: String,
        @FormParam("email") email: String,
        @FormParam("password") password: String,
        @FormParam("confirmPassword") confirmPassword: String,
        @Context uriInfo: UriInfo
    ): Uni<Response>? {
        if (password != confirmPassword) {
            TODO("implement me")
//            TODO: return response returning the form with errors map, map needs to be name of field mapped to error
        }
//        Need to check for failure and handle failure
        return userRepository.createUser(name, email, password).onItem().transform{
//            NOTE: assuming there is a user for now, need to handle error next
            Response.created(uriInfo.absolutePathBuilder.path("/users/login").build()).build()
        }
//        TODO("implement me")
    }
}
