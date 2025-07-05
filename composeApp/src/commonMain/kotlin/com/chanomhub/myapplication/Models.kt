package com.chanomhub.myapplication.models

import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val id: Int,
    val title: String,
    val slug: String,
    val description: String,
    val body: String,
    val ver: String,
    val version: Int,
    val author: Author,
    val favorited: Boolean,
    val favoritesCount: Int,
    val createdAt: String,
    val updatedAt: String,
    val status: String,
    val engine: String,
    val mainImage: String,
    val backgroundImage: String?,
    val coverImage: String?,
    val images: List<String>,
    val sequentialCode: String,
    val tagList: List<String>,
    val categoryList: List<String>,
    val platformList: List<String>
)

@Serializable
data class Author(
    val username: String,
    val bio: String,
    val image: String,
    val backgroundImage: String,
    val following: Boolean,
    val socialMediaLinks: List<String>
)

@Serializable
data class ArticleDetailResponse(
    val article: Article
)

// If you need backward compatibility with old field names, you can create extension properties
val Author.name: String get() = username

// For articles that might have the old structure
@Serializable
data class Tag(
    val id: Int,
    val name: String
)

@Serializable
data class Platform(
    val id: Int,
    val name: String
)

@Serializable
data class Category(
    val id: Int,
    val name: String
)