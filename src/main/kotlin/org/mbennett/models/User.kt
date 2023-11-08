package org.mbennett.models

import java.time.LocalDateTime

class User(
    val id: Long,
    val name: String,
    val email: String,
    val hashedPassword: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {

    //    TODO: need to reimplement this better
    fun authenticate(password: String): Boolean {
        return hashedPassword == password
    }
}
