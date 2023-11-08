package org.mbennett.repositories

import io.quarkus.elytron.security.common.BcryptUtil
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.Row
import io.vertx.mutiny.sqlclient.RowSet
import io.vertx.mutiny.sqlclient.Tuple
import jakarta.enterprise.context.ApplicationScoped
import org.mbennett.models.User

interface UserRepository {
    fun getUser(id: Long): Uni<User>
    fun findUserByEmail(email: String): Uni<User>
    fun createUser(name: String, email: String, password: String): Uni<User>
}

@ApplicationScoped
class UserRepositoryImpl(private val client: PgPool) : UserRepository {
    override fun getUser(id: Long): Uni<User> =
        client.preparedQuery("SELECT id, name, email, hashed_password, created_at, updated_at FROM users WHERE id = $1")
            .execute(Tuple.of(id))
            .onItem().transform(this::handleRowSet)
    override fun findUserByEmail(email: String): Uni<User> =
        client.preparedQuery("SELECT id, name, email, hashed_password, created_at, updated_at FROM users WHERE email = $1")
            .execute(Tuple.of(email))
            .onItem().transform(this::handleRowSet)

    override fun createUser(name: String, email: String, password: String): Uni<User> {
        return client.preparedQuery("INSERT INTO users (name, email, hashed_password) VALUES ($1, $2, $3) RETURNING id, name, email, hashed_password, created_at, updated_at")
            .execute(Tuple.of(name, email, BcryptUtil.bcryptHash(password)))
            .onItem().transform(this::handleRowSet)
    }

    private fun handleRowSet(rowSet: RowSet<Row>): User? {
        val iterator = rowSet.iterator()
        if (iterator.hasNext()) {
            return from(iterator.next())
        }

        return null
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