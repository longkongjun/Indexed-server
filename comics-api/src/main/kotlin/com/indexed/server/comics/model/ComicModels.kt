package com.indexed.server.comics.model

import kotlinx.serialization.Serializable

@Serializable
data class ComicInfo(
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
data class ComicInfoPage(
    val items: List<ComicInfo>,
    val page: Int,
    val size: Int,
    val total: Int
)

@Serializable
data class RecommendGroup(
    val title: String,
    val subtitle: String? = null,
    val style: String,
    val items: List<ComicInfo>
)

@Serializable
data class LastPosition(
    val chapterId: String,
    val chapterTitle: String,
    val pageIndex: Int
)

@Serializable
data class ReadingHistoryItem(
    val comic: ComicInfo,
    val lastReadAt: String,
    val lastPosition: LastPosition
)

@Serializable
data class ReadingHistoryPage(
    val items: List<ReadingHistoryItem>,
    val page: Int,
    val size: Int,
    val total: Int
)

@Serializable
data class ComicMetadata(
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
data class LatestReadingProgress(
    val chapterId: String,
    val chapterTitle: String,
    val pageIndex: Int,
    val lastReadAt: String
)

@Serializable
data class ComicDetail(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val coverImageUrl: String? = null,
    val chapterCount: Int,
    val pageCount: Int,
    val tags: List<String> = emptyList(),
    val updatedAt: String,
    val metadata: ComicMetadata,
    val latestReadingProgress: LatestReadingProgress? = null
)

@Serializable
data class ChapterItem(
    val id: String,
    val title: String,
    val order: Int,
    val pageCount: Int,
    val updatedAt: String
)

@Serializable
data class ChapterSummary(
    val totalChapters: Int,
    val completed: Boolean,
    val lastUpdatedAt: String,
    val chapters: List<ChapterItem>
)

@Serializable
data class ChapterPage(
    val pageIndex: Int,
    val width: Int,
    val height: Int,
    val contentUrl: String
)

@Serializable
data class ChapterContent(
    val pages: List<ChapterPage>
)

@Serializable
data class ErrorResponse(
    val error: String
)
