package org.mbennett

import io.quarkus.qute.CheckedTemplate
import io.quarkus.qute.TemplateInstance
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.RequestScoped
import jakarta.enterprise.inject.Default
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.CookieParam
import jakarta.ws.rs.DefaultValue
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.HeaderParam
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Cookie
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.ResponseBuilder
import jakarta.ws.rs.core.UriInfo
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.mbennett.models.Post
import org.mbennett.models.PostType
import org.mbennett.repositories.PostRepository
import java.util.concurrent.CompletionStage

@Path("/")
class PostResource(
    private val repository: PostRepository,
    @ConfigProperty(name = "greeting.name")
    private val name: String,
) {


    @CheckedTemplate(requireTypeSafeExpressions = false)
    class Templates {
        companion object {
            @JvmStatic
            external fun blog(name: String, posts: List<Post>): TemplateInstance

            @JvmStatic
            external fun labs(name: String, posts: List<Post>): TemplateInstance

            @JvmStatic
            external fun courses(name: String, posts: List<Post>): TemplateInstance

            @JvmStatic
            external fun create(): TemplateInstance

            @JvmStatic
            external fun update(): TemplateInstance

            @JvmStatic
            external fun post(post: Post): TemplateInstance
        }
    }

    @GET
    @Path("blog")
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed("admin")
    fun blog(): Uni<TemplateInstance> = repository
        .getPosts()
        .onItem()
        .transform {
            Templates.blog(name, it)
        }

    @GET
    @Path("labs")
    @Produces(MediaType.TEXT_HTML)
    fun labs(): Uni<TemplateInstance> = repository
        .getPosts(PostType.Lab)
        .onItem()
        .transform {
            Templates.labs(name, it)
        }


    @GET
    @Path("courses")
    @Produces(MediaType.TEXT_HTML)
    fun courses(): Uni<TemplateInstance> = repository
        .getPosts(PostType.Course)
        .onItem()
        .transform {
            Templates.courses(name, it)
        }

    @GET
    @Path("new")
    @Produces(MediaType.TEXT_HTML)
    suspend fun create(): TemplateInstance {
        return Templates.create()
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.TEXT_HTML)
    fun update(id: Int, @Context uriInfo: UriInfo): Uni<Response> =
        repository.getPost(id)
            .onItem().transform {
                if (it != null) {
                    Response.ok(it)
                }

                Response.seeOther(uriInfo.absolutePathBuilder.path("/").build())
            }.onItem().transform { it.build() }

    @POST
    @Path("/posts")
    @Transactional
    fun createPost(
        @FormParam("title") title: String,
        @FormParam("description") description: String,
        @FormParam("type") type: PostType,
        @HeaderParam("HX-Request") hxRequest: Boolean,
        @HeaderParam("X-CSRF-TOKEN") csrfToken: String,
        @CookieParam("csrf-token") csrfCookie: Cookie,
    ): Uni<Response> =
        repository.createPost(title, description, type)
            .onItem().transform{
                println("Cookie Value: ${csrfCookie.value}")
                println("Form Value: $csrfToken")
                if (hxRequest) {
                    Response
                        .ok(Templates.post(it))
                        .header("HX-Trigger", "clear-post-add")
                        .header("Location", "/blog")
                        .build()
                } else {
                    Response.status(Response.Status.FOUND).header("Location", "/blog").build()
                }
            }

}