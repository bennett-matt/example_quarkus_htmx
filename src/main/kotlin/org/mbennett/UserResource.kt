package org.mbennett

import io.quarkus.qute.CheckedTemplate
import io.quarkus.qute.TemplateInstance
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/users")
class UserResource {
    @CheckedTemplate
    class Templates {
        companion object {
            @JvmStatic
            external fun login(): TemplateInstance
            @JvmStatic
            external fun signUp(): TemplateInstance
        }
    }

    @GET
    @Path("login")
    @Produces(MediaType.TEXT_HTML)
    suspend fun login(): TemplateInstance {
        return Templates.login()
    }

    @GET
    @Path("sign-up")
    @Produces(MediaType.TEXT_HTML)
    suspend fun signUp(): TemplateInstance {
        return Templates.signUp()
    }

    @POST
    @Path("login")
    suspend fun loginPost(@FormParam("name") name: String, @FormParam("email") email: String, @FormParam("password") password: String) {
        TODO("implement me")
    }

    @POST
    @Path("sign-up")
    suspend fun signUpPost(@FormParam("email") email: String, @FormParam("password") password: String) {
        TODO("implement me")
    }
}
