package com.indexed.server.comics.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ComicInfoResponse(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val coverImageUrl: String? = null,
    val chapterCount: Int,
    val pageCount: Int,
    val rating: Double? = null,
    val tags: List<String> = emptyList(),
    val updatedAt: String
)

@Serializable
data class ComicInfoPageResponse(
    val items: List<ComicInfoResponse>,
    val page: Int,
    val size: Int,
    val total: Int
)

@Serializable
data class RecommendGroupResponse(
    val title: String,
    val subtitle: String? = null,
    val style: String,
    val items: List<ComicInfoResponse>
)

@Serializable
data class LastPositionResponse(
    val chapterId: String,
    val chapterTitle: String,
    val pageIndex: Int
)

@Serializable
data class ReadingHistoryItemResponse(
    val comic: ComicInfoResponse,
    val lastReadAt: String,
    val lastPosition: LastPositionResponse
)

@Serializable
data class ReadingHistoryPageResponse(
    val items: List<ReadingHistoryItemResponse>,
    val page: Int,
    val size: Int,
    val total: Int
)

@Serializable
data class ComicMetadataResponse(
    val title: String,
    val originalTitle: String? = null,
    val author: String? = null,
    val description: String? = null,
    val publisher: String? = null,
    val releaseYear: Int? = null,
    val language: String? = null,
    val tags: List<String> = emptyList()
)

@Serializable
data class LatestReadingProgressResponse(
    val chapterId: String,
    val chapterTitle: String,
    val pageIndex: Int,
    val lastReadAt: String
)

@Serializable
data class ComicDetailResponse(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val coverImageUrl: String? = null,
    val chapterCount: Int,
    val pageCount: Int,
    val tags: List<String> = emptyList(),
    val updatedAt: String,
    val metadata: ComicMetadataResponse,
    val latestReadingProgress: LatestReadingProgressResponse? = null
)

@Serializable
data class ChapterItemResponse(
    val id: String,
    val title: String,
    val order: Int,
    val pageCount: Int,
    val updatedAt: String
)

@Serializable
data class ChapterSummaryResponse(
    val totalChapters: Int,
    val completed: Boolean,
    val lastUpdatedAt: String,
    val chapters: List<ChapterItemResponse>
)

@Serializable
data class ChapterPageResponse(
    val pageIndex: Int,
    val width: Int,
    val height: Int,
    val contentUrl: String
)

@Serializable
data class ChapterContentResponse(
    val pages: List<ChapterPageResponse>
)

@Serializable
data class ErrorResponse(
    val error: String
)
