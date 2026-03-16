package com.indexed.server.comics.service

import com.indexed.server.comics.mock.MockComic
import com.indexed.server.comics.mock.MockComicsData
import com.indexed.server.comics.mock.MockReadingHistory
import com.indexed.server.comics.model.ChapterContent
import com.indexed.server.comics.model.ChapterItem
import com.indexed.server.comics.model.ChapterPage
import com.indexed.server.comics.model.ChapterSummary
import com.indexed.server.comics.model.ComicDetail
import com.indexed.server.comics.model.ComicInfo
import com.indexed.server.comics.model.ComicInfoPage
import com.indexed.server.comics.model.ComicMetadata
import com.indexed.server.comics.model.LastPosition
import com.indexed.server.comics.model.LatestReadingProgress
import com.indexed.server.comics.model.ReadingHistoryItem
import com.indexed.server.comics.model.ReadingHistoryPage
import com.indexed.server.comics.model.RecommendGroup

class ComicsService(
    private val comics: List<MockComic> = MockComicsData.comics,
    private val history: List<MockReadingHistory> = MockComicsData.readingHistory
) {
    fun listComics(client: String, page: Int, size: Int, sort: String, keyword: String?): ComicInfoPage {
        val filtered = comics.asSequence()
            .filter {
                keyword.isNullOrBlank() || it.title.contains(keyword, ignoreCase = true) ||
                    it.subtitle.contains(keyword, ignoreCase = true)
            }
            .toList()

        val sorted = when (sort) {
            "rating" -> filtered.sortedByDescending { it.rating }
            "updatedAt" -> filtered.sortedByDescending { it.updatedAt }
            "createdAt" -> filtered.sortedByDescending { it.createdAt }
            else -> filtered.sortedByDescending { it.chapterCount * 1000L + it.pageCount }
        }

        return toComicPage(sorted, client, page, size)
    }

    fun listRecommended(client: String): List<RecommendGroup> {
        return MockComicsData.recommendedGroups.map { group ->
            val groupItems = group.comicIds.mapNotNull { comicId ->
                comics.find { it.id == comicId }?.toComicInfo(client)
            }
            RecommendGroup(
                title = group.title,
                subtitle = group.subtitle,
                style = group.style,
                items = groupItems
            )
        }
    }

    fun listRecent(client: String, page: Int, size: Int): ComicInfoPage {
        val sorted = comics.sortedByDescending { it.updatedAt }
        return toComicPage(sorted, client, page, size)
    }

    fun listReadingHistory(client: String, page: Int, size: Int): ReadingHistoryPage {
        val sortedHistory = history.sortedByDescending { it.lastReadAt }
        val paged = paginate(sortedHistory, page, size)
        val items = paged.mapNotNull { record ->
            val comic = comics.find { it.id == record.comicId } ?: return@mapNotNull null
            val chapter = comic.chapters.find { it.id == record.chapterId } ?: return@mapNotNull null
            ReadingHistoryItem(
                comic = comic.toComicInfo(client),
                lastReadAt = record.lastReadAt,
                lastPosition = LastPosition(
                    chapterId = chapter.id,
                    chapterTitle = chapter.title,
                    pageIndex = record.pageIndex
                )
            )
        }
        return ReadingHistoryPage(items = items, page = page, size = size, total = sortedHistory.size)
    }

    fun getComicDetail(client: String, comicId: String): ComicDetail? {
        val comic = comics.find { it.id == comicId } ?: return null
        val latestHistory = history
            .filter { it.comicId == comicId }
            .maxByOrNull { it.lastReadAt }
        val latestProgress = latestHistory?.let { progress ->
            val chapter = comic.chapters.find { it.id == progress.chapterId } ?: return@let null
            LatestReadingProgress(
                chapterId = chapter.id,
                chapterTitle = chapter.title,
                pageIndex = progress.pageIndex,
                lastReadAt = progress.lastReadAt
            )
        }

        return ComicDetail(
            id = comic.id,
            title = comic.title,
            subtitle = comic.subtitle,
            coverImageUrl = "/$client/pages/${comic.coverPageId}/content",
            chapterCount = comic.chapterCount,
            pageCount = comic.pageCount,
            tags = comic.tags,
            updatedAt = comic.updatedAt,
            metadata = ComicMetadata(
                title = comic.metadata.title,
                originalTitle = comic.metadata.originalTitle,
                author = comic.metadata.author,
                description = comic.metadata.description,
                publisher = comic.metadata.publisher,
                releaseYear = comic.metadata.releaseYear,
                language = comic.metadata.language,
                tags = comic.metadata.tags
            ),
            latestReadingProgress = latestProgress
        )
    }

    fun getChapterSummary(comicId: String): ChapterSummary? {
        val comic = comics.find { it.id == comicId } ?: return null
        val chapters = comic.chapters.sortedByDescending { it.order }.map { chapter ->
            ChapterItem(
                id = chapter.id,
                title = chapter.title,
                order = chapter.order,
                pageCount = chapter.pageCount,
                updatedAt = chapter.updatedAt
            )
        }
        return ChapterSummary(
            totalChapters = comic.chapterCount,
            completed = comic.completed,
            lastUpdatedAt = comic.updatedAt,
            chapters = chapters
        )
    }

    fun getChapterContent(client: String, chapterId: String): ChapterContent? {
        val chapter = comics.flatMap { it.chapters }.find { it.id == chapterId } ?: return null
        val pages = chapter.pages.mapIndexed { index, page ->
            ChapterPage(
                pageIndex = index,
                width = page.width,
                height = page.height,
                contentUrl = "/$client/pages/${page.pageId}/content"
            )
        }
        return ChapterContent(pages = pages)
    }

    private fun MockComic.toComicInfo(client: String): ComicInfo {
        return ComicInfo(
            id = id,
            title = title,
            subtitle = subtitle,
            coverImageUrl = "/$client/pages/$coverPageId/content",
            chapterCount = chapterCount,
            pageCount = pageCount,
            rating = rating,
            tags = tags,
            updatedAt = updatedAt
        )
    }

    private fun toComicPage(source: List<MockComic>, client: String, page: Int, size: Int): ComicInfoPage {
        val safePage = page.coerceAtLeast(0)
        val safeSize = size.coerceAtLeast(1)
        val pagedItems = paginate(source, safePage, safeSize).map { it.toComicInfo(client) }
        return ComicInfoPage(
            items = pagedItems,
            page = safePage,
            size = safeSize,
            total = source.size
        )
    }

    private fun <T> paginate(items: List<T>, page: Int, size: Int): List<T> {
        val fromIndex = page * size
        if (fromIndex >= items.size) return emptyList()
        val toIndex = (fromIndex + size).coerceAtMost(items.size)
        return items.subList(fromIndex, toIndex)
    }
}
