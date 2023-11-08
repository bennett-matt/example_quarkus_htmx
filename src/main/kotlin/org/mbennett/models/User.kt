package org.mbennett.models

//import org.wildfly.security.password.Password
import io.quarkus.security.jpa.Password
import io.quarkus.security.jpa.PasswordType
import java.security.Principal
import java.time.LocalDateTime

class User(
    val id: Long,
    val name: String,
    val email: String,
    @Password(value = PasswordType.MCF)
    val hashedPassword: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    fun to(): Map<String, Any> =
        mapOf(
            "id" to id,
            "name" to name,
            "email" to email,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt)

    companion object {
        fun from(data: Map<String, Any>) =
            User(
                data["id"] as Long,
                data["name"] as String,
                data["email"] as String,
                "",
                data["createdAt"] as LocalDateTime,
                data["updatedAt"] as LocalDateTime
            )
    }
}
