package org.mbennett.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

enum class PostType {
    Blog,
    Course,
    Lab,
}

@Serializable
data class Post(
    val id: Long,
    val title: String,
    val description: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val publishedAt: LocalDateTime?,
    val type: PostType = PostType.Blog,
);