package com.indexed.server.comics.domain.model

data class Comic(
    val id: String,
    val title: String,
    val subtitle: String?,
    val coverImageUrl: String?,
    val chapterCount: Int,
    val pageCount: Int,
    val rating: Double?,
    val tags: List<String>,
    val updatedAt: String
)

data class ComicPage(
    val items: List<Comic>,
    val page: Int,
    val size: Int,
    val total: Int
)

data class RecommendSection(
    val title: String,
    val subtitle: String?,
    val style: String,
    val items: List<Comic>
)

data class LastPosition(
    val chapterId: String,
    val chapterTitle: String,
    val pageIndex: Int
)

data class ReadingHistory(
    val comic: Comic,
    val lastReadAt: String,
    val lastPosition: LastPosition
)

data class ReadingHistoryPage(
    val items: List<ReadingHistory>,
    val page: Int,
    val size: Int,
    val total: Int
)

data class ComicMetadata(
    val title: String,
    val originalTitle: String?,
    val author: String?,
    val description: String?,
    val publisher: String?,
    val releaseYear: Int?,
    val language: String?,
    val tags: List<String>
)

data class LatestReadingProgress(
    val chapterId: String,
    val chapterTitle: String,
    val pageIndex: Int,
    val lastReadAt: String
)

data class ComicDetail(
    val id: String,
    val title: String,
    val subtitle: String?,
    val coverImageUrl: String?,
    val chapterCount: Int,
    val pageCount: Int,
    val tags: List<String>,
    val updatedAt: String,
    val metadata: ComicMetadata,
    val latestReadingProgress: LatestReadingProgress?
)

data class Chapter(
    val id: String,
    val title: String,
    val order: Int,
    val pageCount: Int,
    val updatedAt: String
)

data class ChapterSummary(
    val totalChapters: Int,
    val completed: Boolean,
    val lastUpdatedAt: String,
    val chapters: List<Chapter>
)

data class ChapterPage(
    val pageIndex: Int,
    val width: Int,
    val height: Int,
    val contentUrl: String
)

data class ChapterContent(
    val pages: List<ChapterPage>
)
