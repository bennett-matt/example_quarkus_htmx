package org.mbennett.models

data class UserAuthForm(
    val name: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val validator: Validator
)
