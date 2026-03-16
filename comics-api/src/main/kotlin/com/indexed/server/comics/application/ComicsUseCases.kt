package com.indexed.server.comics.application

import com.indexed.server.comics.domain.model.ChapterContent
import com.indexed.server.comics.domain.model.ChapterSummary
import com.indexed.server.comics.domain.model.ComicDetail
import com.indexed.server.comics.domain.model.ComicError
import com.indexed.server.comics.domain.model.ComicListQuery
import com.indexed.server.comics.domain.model.ComicPage
import com.indexed.server.comics.domain.model.PagingQuery
import com.indexed.server.comics.domain.model.ReadingHistoryPage
import com.indexed.server.comics.domain.model.RecommendSection
import com.indexed.server.comics.domain.repository.ComicCatalogRepository

sealed class UseCaseResult<out T> {
    data class Ok<T>(val value: T) : UseCaseResult<T>()
    data class Err(val error: ComicError) : UseCaseResult<Nothing>()
}

class ListComicsUseCase(private val repository: ComicCatalogRepository) {
    suspend operator fun invoke(client: String, query: ComicListQuery): UseCaseResult<ComicPage> {
        return runCatching { repository.listComics(client, query) }
            .fold(
                onSuccess = { UseCaseResult.Ok(it) },
                onFailure = { UseCaseResult.Err(ComicError.UpstreamUnavailable(it.message ?: "上游服务不可用")) }
            )
    }
}

class ListRecommendedUseCase(private val repository: ComicCatalogRepository) {
    suspend operator fun invoke(client: String): UseCaseResult<List<RecommendSection>> {
        return runCatching { repository.listRecommended(client) }
            .fold(
                onSuccess = { UseCaseResult.Ok(it) },
                onFailure = { UseCaseResult.Err(ComicError.UpstreamUnavailable(it.message ?: "上游服务不可用")) }
            )
    }
}

class ListRecentUseCase(private val repository: ComicCatalogRepository) {
    suspend operator fun invoke(client: String, query: PagingQuery): UseCaseResult<ComicPage> {
        return runCatching { repository.listRecent(client, query) }
            .fold(
                onSuccess = { UseCaseResult.Ok(it) },
                onFailure = { UseCaseResult.Err(ComicError.UpstreamUnavailable(it.message ?: "上游服务不可用")) }
            )
    }
}

class ListReadingHistoryUseCase(private val repository: ComicCatalogRepository) {
    suspend operator fun invoke(query: PagingQuery): UseCaseResult<ReadingHistoryPage> {
        return runCatching { repository.listReadingHistory(query) }
            .fold(
                onSuccess = { UseCaseResult.Ok(it) },
                onFailure = { UseCaseResult.Err(ComicError.UpstreamUnavailable(it.message ?: "上游服务不可用")) }
            )
    }
}

class GetComicDetailUseCase(private val repository: ComicCatalogRepository) {
    suspend operator fun invoke(client: String, comicId: String): UseCaseResult<ComicDetail> {
        val detail = runCatching { repository.getComicDetail(client, comicId) }
            .getOrElse { return UseCaseResult.Err(ComicError.UpstreamUnavailable(it.message ?: "上游服务不可用")) }
        return if (detail == null) {
            UseCaseResult.Err(ComicError.NotFound("漫画不存在: $comicId"))
        } else {
            UseCaseResult.Ok(detail)
        }
    }
}

class GetChapterSummaryUseCase(private val repository: ComicCatalogRepository) {
    suspend operator fun invoke(comicId: String): UseCaseResult<ChapterSummary> {
        val summary = runCatching { repository.getChapterSummary(comicId) }
            .getOrElse { return UseCaseResult.Err(ComicError.UpstreamUnavailable(it.message ?: "上游服务不可用")) }
        return if (summary == null) {
            UseCaseResult.Err(ComicError.NotFound("漫画不存在: $comicId"))
        } else {
            UseCaseResult.Ok(summary)
        }
    }
}

class GetChapterContentUseCase(private val repository: ComicCatalogRepository) {
    suspend operator fun invoke(chapterId: String): UseCaseResult<ChapterContent> {
        val content = runCatching { repository.getChapterContent(chapterId) }
            .getOrElse { return UseCaseResult.Err(ComicError.UpstreamUnavailable(it.message ?: "上游服务不可用")) }
        return if (content == null) {
            UseCaseResult.Err(ComicError.NotFound("章节不存在: $chapterId"))
        } else {
            UseCaseResult.Ok(content)
        }
    }
}

data class ComicsUseCases(
    val listComics: ListComicsUseCase,
    val listRecommended: ListRecommendedUseCase,
    val listRecent: ListRecentUseCase,
    val listReadingHistory: ListReadingHistoryUseCase,
    val getComicDetail: GetComicDetailUseCase,
    val getChapterSummary: GetChapterSummaryUseCase,
    val getChapterContent: GetChapterContentUseCase
)

fun buildComicsUseCases(repository: ComicCatalogRepository): ComicsUseCases {
    return ComicsUseCases(
        listComics = ListComicsUseCase(repository),
        listRecommended = ListRecommendedUseCase(repository),
        listRecent = ListRecentUseCase(repository),
        listReadingHistory = ListReadingHistoryUseCase(repository),
        getComicDetail = GetComicDetailUseCase(repository),
        getChapterSummary = GetChapterSummaryUseCase(repository),
        getChapterContent = GetChapterContentUseCase(repository)
    )
}
