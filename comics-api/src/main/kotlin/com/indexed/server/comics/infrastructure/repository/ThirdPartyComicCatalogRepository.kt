package com.indexed.server.comics.infrastructure.repository

import com.indexed.server.comics.domain.model.Chapter
import com.indexed.server.comics.domain.model.ChapterContent
import com.indexed.server.comics.domain.model.ChapterPage
import com.indexed.server.comics.domain.model.ChapterSummary
import com.indexed.server.comics.domain.model.Comic
import com.indexed.server.comics.domain.model.ComicDetail
import com.indexed.server.comics.domain.model.ComicListQuery
import com.indexed.server.comics.domain.model.ComicMetadata
import com.indexed.server.comics.domain.model.ComicPage
import com.indexed.server.comics.domain.model.PagingQuery
import com.indexed.server.comics.domain.model.ReadingHistoryPage
import com.indexed.server.comics.domain.model.RecommendSection
import com.indexed.server.comics.domain.repository.ComicCatalogRepository
import com.indexed.server.thirdparty.api.ComicThirdPartyApi
import com.indexed.server.thirdparty.api.ThirdPartyChapter
import com.indexed.server.thirdparty.api.ThirdPartyComic
import com.indexed.server.thirdparty.api.ThirdPartySearchQuery

class ThirdPartyComicCatalogRepository(
    private val thirdPartyApi: ComicThirdPartyApi
) : ComicCatalogRepository {

    override suspend fun listComics(client: String, query: ComicListQuery): ComicPage {
        val orderBy = when (query.sort) {
            "rating" -> "score"
            "updatedAt", "createdAt" -> "start_date"
            else -> "popularity"
        }
        val result = thirdPartyApi.searchComics(
            ThirdPartySearchQuery(
                page = query.page,
                size = query.size,
                keyword = query.keyword,
                orderBy = orderBy
            )
        )
        return ComicPage(
            items = result.items.map { it.toComic() },
            page = query.page,
            size = query.size,
            total = result.total
        )
    }

    override suspend fun listRecommended(client: String): List<RecommendSection> {
        val hotItems = thirdPartyApi.listRecommendations()
            .take(12)
            .map {
                Comic(
                    id = it.id,
                    title = it.title ?: "Unknown",
                    subtitle = null,
                    coverImageUrl = it.coverImageUrl,
                    chapterCount = 0,
                    pageCount = 0,
                    rating = null,
                    tags = emptyList(),
                    updatedAt = ""
                )
            }

        val latest = thirdPartyApi.searchComics(
            ThirdPartySearchQuery(page = 0, size = 10, keyword = null, orderBy = "start_date")
        )
        return listOf(
            RecommendSection(
                title = "热门推荐",
                subtitle = "来自 Jikan 推荐流",
                style = "horizontal_scroll",
                items = hotItems
            ),
            RecommendSection(
                title = "最近上架",
                subtitle = "按发布时间排序",
                style = "grid",
                items = latest.items.map { it.toComic() }
            )
        )
    }

    override suspend fun listRecent(client: String, query: PagingQuery): ComicPage {
        val result = thirdPartyApi.searchComics(
            ThirdPartySearchQuery(page = query.page, size = query.size, keyword = null, orderBy = "start_date")
        )
        return ComicPage(
            items = result.items.map { it.toComic() },
            page = query.page,
            size = query.size,
            total = result.total
        )
    }

    override suspend fun listReadingHistory(query: PagingQuery): ReadingHistoryPage {
        return ReadingHistoryPage(items = emptyList(), page = query.page, size = query.size, total = 0)
    }

    override suspend fun getComicDetail(client: String, comicId: String): ComicDetail? {
        val manga = thirdPartyApi.getComicDetail(comicId) ?: return null
        return ComicDetail(
            id = manga.id,
            title = manga.title ?: "Unknown",
            subtitle = manga.titleEnglish ?: manga.titleOriginal,
            coverImageUrl = manga.coverImageUrl,
            chapterCount = manga.chapterCount ?: 0,
            pageCount = (manga.chapterCount ?: 0) * 20,
            tags = manga.collectTags(),
            updatedAt = manga.publishedTo ?: manga.publishedFrom ?: "",
            metadata = ComicMetadata(
                title = manga.title ?: "Unknown",
                originalTitle = manga.titleOriginal,
                author = manga.authors.joinToString().ifBlank { null },
                description = manga.synopsis,
                publisher = manga.publisher,
                releaseYear = manga.publishedFrom?.take(4)?.toIntOrNull(),
                language = "ja",
                tags = manga.collectTags()
            ),
            latestReadingProgress = null
        )
    }

    override suspend fun getChapterSummary(comicId: String): ChapterSummary? {
        val manga = thirdPartyApi.getComicDetail(comicId) ?: return null
        val chapters = thirdPartyApi.listComicChapters(comicId) ?: return null
        val mapped = chapters.map { it.toChapter(comicId) }.sortedByDescending { it.order }
        return ChapterSummary(
            totalChapters = manga.chapterCount ?: mapped.size,
            completed = (manga.status ?: "").contains("finished", ignoreCase = true),
            lastUpdatedAt = manga.publishedTo ?: manga.publishedFrom ?: "",
            chapters = mapped
        )
    }

    override suspend fun getChapterContent(chapterId: String): ChapterContent? {
        val parsed = parseChapterId(chapterId) ?: return null
        val chapters = thirdPartyApi.listComicChapters(parsed.comicId) ?: return null
        val chapter = chapters.firstOrNull { it.chapterNumber == parsed.chapterNumber } ?: return null
        val contentUrl = chapter.contentUrl ?: return null
        return ChapterContent(
            pages = listOf(
                ChapterPage(
                    pageIndex = 0,
                    width = 0,
                    height = 0,
                    contentUrl = contentUrl
                )
            )
        )
    }

    private fun ThirdPartyComic.toComic(): Comic {
        return Comic(
            id = id,
            title = title ?: "Unknown",
            subtitle = titleEnglish ?: titleOriginal,
            coverImageUrl = coverImageUrl,
            chapterCount = chapterCount ?: 0,
            pageCount = (chapterCount ?: 0) * 20,
            rating = score,
            tags = collectTags(),
            updatedAt = publishedTo ?: publishedFrom ?: ""
        )
    }

    private fun ThirdPartyComic.collectTags(): List<String> {
        return tags.distinct().take(5)
    }

    private fun ThirdPartyChapter.toChapter(comicId: String): Chapter {
        return Chapter(
            id = "manga-$comicId-chapter-$chapterNumber",
            title = title ?: "Chapter $chapterNumber",
            order = chapterNumber,
            pageCount = 0,
            updatedAt = updatedAt ?: ""
        )
    }

    private data class ParsedChapterId(
        val comicId: String,
        val chapterNumber: Int
    )

    private fun parseChapterId(chapterId: String): ParsedChapterId? {
        val parts = chapterId.split("-")
        if (parts.size != 4 || parts[0] != "manga" || parts[2] != "chapter") return null
        val chapterNumber = parts[3].toIntOrNull() ?: return null
        return ParsedChapterId(parts[1], chapterNumber)
    }
}
