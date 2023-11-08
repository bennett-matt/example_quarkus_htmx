package org.mbennett.repositories

import io.smallrye.mutiny.Uni
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.Row
import io.vertx.mutiny.sqlclient.Tuple
import jakarta.enterprise.context.ApplicationScoped
import org.mbennett.models.User

interface UserRepository {
    fun findUserByEmail(email: String): Uni<User>
}

@ApplicationScoped
class UserRepositoryImpl(private val client: PgPool) : UserRepository {
    override fun findUserByEmail(email: String): Uni<User> =
        client.preparedQuery("SELECT id, name, email, hashed_password, created_at, updated_at FROM users WHERE email = $1")
            .execute(Tuple.of(email))
            .onItem().transform {it.iterator()}
            .onItem().transform{
                if (it.hasNext()) {
                    from(it.next())
                } else {
                    null
                }
            }

    private fun from(row: Row): User =
        User(
            row.getLong("id"),
            row.getString("name"),
            row.getString("email"),
            row.getString("hashed_password"),
            row.getLocalDateTime("created_at"),
            row.getLocalDateTime("updated_at")
        )
}