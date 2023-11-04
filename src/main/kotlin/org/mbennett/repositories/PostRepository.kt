package org.mbennett.repositories

import io.smallrye.mutiny.Uni
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.Tuple
import jakarta.enterprise.context.ApplicationScoped
import org.mbennett.models.Post
import org.mbennett.models.PostType
import org.mbennett.models.PostType.Blog

interface PostRepository {
    suspend fun getPosts(type: PostType = Blog): Uni<List<Post>>
}

@ApplicationScoped
class PostRepositoryImpl(
    private val client: PgPool
) : PostRepository {
    override suspend fun getPosts(type: PostType): Uni<List<Post>> =  client
        .preparedQuery("""
            SELECT id, title, description, type, created_at, updated_at, published_at 
            FROM posts WHERE type = $1;
        """.trimIndent()
        )
        .execute(Tuple.of(type.toString()))
        .onItem()
        .transform {
            it.map {row ->
                Post(
                    row.getLong("id"),
                    row.getString("title"),
                    row.getString("description"),
                    row.getLocalDateTime("created_at"),
                    row.getLocalDateTime("updated_at"),
                    row.getLocalDateTime("published_at"),
                    PostType.valueOf(row.getString("type"))
                )
            }
        }
}
