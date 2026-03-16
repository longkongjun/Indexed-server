package com.indexed.server.thirdparty.jikan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JikanListResponse<T>(
    val data: List<T> = emptyList(),
    val pagination: JikanPagination? = null
)

@Serializable
data class JikanSingleResponse<T>(
    val data: T
)

@Serializable
data class JikanPagination(
    @SerialName("last_visible_page")
    val lastVisiblePage: Int? = null,
    @SerialName("has_next_page")
    val hasNextPage: Boolean = false,
    val items: JikanPaginationItems? = null
)

@Serializable
data class JikanPaginationItems(
    val total: Int? = null
)

@Serializable
data class JikanImageSet(
    val jpg: JikanImage? = null
)

@Serializable
data class JikanImage(
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("large_image_url")
    val largeImageUrl: String? = null
)

@Serializable
data class JikanNamedResource(
    val name: String? = null
)

@Serializable
data class JikanAuthorResource(
    val name: String? = null
)

@Serializable
data class JikanDateRange(
    val from: String? = null,
    val to: String? = null
)

@Serializable
data class JikanManga(
    @SerialName("mal_id")
    val malId: Int,
    val title: String? = null,
    @SerialName("title_english")
    val titleEnglish: String? = null,
    @SerialName("title_japanese")
    val titleJapanese: String? = null,
    val synopsis: String? = null,
    val chapters: Int? = null,
    val score: Double? = null,
    val genres: List<JikanNamedResource> = emptyList(),
    val themes: List<JikanNamedResource> = emptyList(),
    val demographics: List<JikanNamedResource> = emptyList(),
    val images: JikanImageSet? = null,
    val published: JikanDateRange? = null,
    val authors: List<JikanAuthorResource> = emptyList(),
    val serializations: List<JikanNamedResource> = emptyList(),
    val status: String? = null,
    val url: String? = null
)

@Serializable
data class JikanRecommendationBlock(
    val entry: List<JikanRecommendationEntry> = emptyList()
)

@Serializable
data class JikanRecommendationEntry(
    @SerialName("mal_id")
    val malId: Int,
    val title: String? = null,
    val images: JikanImageSet? = null
)

@Serializable
data class JikanChapter(
    val chapter: String? = null,
    val title: String? = null,
    val date: String? = null,
    val url: String? = null
)
