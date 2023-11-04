package org.mbennett

import io.quarkus.qute.CheckedTemplate
import io.quarkus.qute.TemplateInstance
import io.smallrye.mutiny.Uni
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.mbennett.models.Post
import org.mbennett.models.PostType
import org.mbennett.repositories.PostRepository

@Path("/posts")
class PostResource(
    private val repository: PostRepository,
    @ConfigProperty(name = "greeting.name")
    private val name: String,
) {

    @CheckedTemplate
    class Templates {
        companion object {
            @JvmStatic
            external fun blog(name: String, posts: Uni<List<Post>>): TemplateInstance
            @JvmStatic
            external fun labs(name: String, posts: Uni<List<Post>>): TemplateInstance
            @JvmStatic
            external fun courses(name: String, posts: Uni<List<Post>>): TemplateInstance
            @JvmStatic
            external fun create(): TemplateInstance
            @JvmStatic
            external fun update(): TemplateInstance
        }
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    suspend fun blog(): TemplateInstance {
        return Templates.blog(name, repository.getPosts(type = PostType.Blog))
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    suspend fun labs(): TemplateInstance {
        return Templates.labs(name, repository.getPosts(type = PostType.Lab))
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    suspend fun courses(): TemplateInstance {
        return Templates.courses(name, repository.getPosts(type = PostType.Course))
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    suspend fun create(): TemplateInstance {
        return Templates.courses(name, repository.getPosts(type = PostType.Course))
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    suspend fun update(): TemplateInstance {
        return Templates.courses(name, repository.getPosts(type = PostType.Course))
    }

    @POST
    suspend fun createPost() {

    }
}