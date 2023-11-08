package org.mbennett.repositories

import io.smallrye.mutiny.Uni
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.Row
import io.vertx.mutiny.sqlclient.RowSet
import io.vertx.mutiny.sqlclient.Tuple
import jakarta.enterprise.context.ApplicationScoped
import org.mbennett.models.Post
import org.mbennett.models.PostType
import org.mbennett.models.PostType.Blog

interface PostRepository {
    fun getPosts(type: PostType = Blog): Uni<List<Post>>
    fun getPost(id: Int): Uni<Post>
    fun createPost(title: String, description: String, type: PostType): Uni<Post>
}

@ApplicationScoped
class PostRepositoryImpl(
    private val client: PgPool
) : PostRepository {
    override fun getPosts(type: PostType): Uni<List<Post>> =  client
        .preparedQuery("""
            SELECT id, title, description, type, created_at, updated_at, published_at 
            FROM posts WHERE type = $1;
        """.trimIndent()
        )
        .execute(Tuple.of(type.toString()))
        .onItem()
        .transform {it.map {row -> from(row)}}

    override fun getPost(id: Int): Uni<Post> = client
        .preparedQuery(
            "SELECT id, title, description, type, created_at, updated_at, published_at FROM posts WHERE id = $1"
        ).execute(Tuple.of(id))
            .onItem().transform{it.iterator()}
            .onItem().transform {
                if (it.hasNext()) {
                    from(it.next())
                } else {
                    null
                }
            }

    override fun createPost(title: String, description: String, type: PostType): Uni<Post> = client
        .preparedQuery("INSERT INTO posts (title, description, type) VALUES ($1, $2, $3) RETURNING id, created_at, updated_at")
        .execute(Tuple.of(title, description, type))
        .onItem().transform {
            val vals = it.iterator().next()
            Post(
                vals.getLong("id"),
                title,
                description,
                vals.getLocalDateTime("created_at"),
                vals.getLocalDateTime("updated_at"),
                null,
                type
            )
        }

    private fun from(row: Row): Post = Post(
        row.getLong("id"),
        row.getString("title"),
        row.getString("description"),
        row.getLocalDateTime("created_at"),
        row.getLocalDateTime("updated_at"),
        row.getLocalDateTime("published_at"),
        PostType.valueOf(row.getString("type"))
    )
}
