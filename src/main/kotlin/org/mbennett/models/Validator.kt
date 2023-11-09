package org.mbennett.models

import java.util.regex.Pattern

data class Validator(
    val fieldErrors: MutableMap<String, String> = mutableMapOf(),
    val nonFieldErrors: MutableList<String> = mutableListOf(),
) {
    fun valid(): Boolean {
        return fieldErrors.isEmpty() && nonFieldErrors.isEmpty()
    }

    fun addFieldError(key: String, message: String) {
        if (!fieldErrors.containsKey(key)) {
            fieldErrors[key] = message
        }
    }

    fun addNonFieldError(message: String) {
        nonFieldErrors.add(message)
    }

    fun checkField(ok: Boolean, key: String, message: String) {
        if (!ok) {
            addFieldError(key, message)
        }
    }
}

val emailRX: Pattern = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+\\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$")

fun notBlank(value: String): Boolean {
    return value.trim().isNotEmpty()
}

fun maxChars(value: String, n: Int): Boolean {
    return value.length <= n
}

fun permittedInt(value: Int, vararg permittedValues: Int): Boolean {
    return permittedValues.contains(value)
}

fun minChars(value: String, n: Int): Boolean {
    return value.length >= n
}

fun matches(value: String, rx: Pattern): Boolean {
    return rx.matcher(value).matches()
}
