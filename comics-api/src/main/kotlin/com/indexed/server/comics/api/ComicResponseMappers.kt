package com.indexed.server.comics.api

import com.indexed.server.comics.api.dto.ChapterContentResponse
import com.indexed.server.comics.api.dto.ChapterItemResponse
import com.indexed.server.comics.api.dto.ChapterPageResponse
import com.indexed.server.comics.api.dto.ChapterSummaryResponse
import com.indexed.server.comics.api.dto.ComicDetailResponse
import com.indexed.server.comics.api.dto.ComicInfoPageResponse
import com.indexed.server.comics.api.dto.ComicInfoResponse
import com.indexed.server.comics.api.dto.ComicMetadataResponse
import com.indexed.server.comics.api.dto.LastPositionResponse
import com.indexed.server.comics.api.dto.LatestReadingProgressResponse
import com.indexed.server.comics.api.dto.ReadingHistoryItemResponse
import com.indexed.server.comics.api.dto.ReadingHistoryPageResponse
import com.indexed.server.comics.api.dto.RecommendGroupResponse
import com.indexed.server.comics.domain.model.ChapterContent
import com.indexed.server.comics.domain.model.ChapterSummary
import com.indexed.server.comics.domain.model.Comic
import com.indexed.server.comics.domain.model.ComicDetail
import com.indexed.server.comics.domain.model.ComicPage
import com.indexed.server.comics.domain.model.ReadingHistoryPage
import com.indexed.server.comics.domain.model.RecommendSection

fun ComicPage.toResponse(): ComicInfoPageResponse {
    return ComicInfoPageResponse(
        items = items.map { it.toResponse() },
        page = page,
        size = size,
        total = total
    )
}

fun Comic.toResponse(): ComicInfoResponse {
    return ComicInfoResponse(
        id = id,
        title = title,
        subtitle = subtitle,
        coverImageUrl = coverImageUrl,
        chapterCount = chapterCount,
        pageCount = pageCount,
        rating = rating,
        tags = tags,
        updatedAt = updatedAt
    )
}

fun List<RecommendSection>.toResponse(): List<RecommendGroupResponse> {
    return map { section ->
        RecommendGroupResponse(
            title = section.title,
            subtitle = section.subtitle,
            style = section.style,
            items = section.items.map { it.toResponse() }
        )
    }
}

fun ReadingHistoryPage.toResponse(): ReadingHistoryPageResponse {
    return ReadingHistoryPageResponse(
        items = items.map { item ->
            ReadingHistoryItemResponse(
                comic = item.comic.toResponse(),
                lastReadAt = item.lastReadAt,
                lastPosition = LastPositionResponse(
                    chapterId = item.lastPosition.chapterId,
                    chapterTitle = item.lastPosition.chapterTitle,
                    pageIndex = item.lastPosition.pageIndex
                )
            )
        },
        page = page,
        size = size,
        total = total
    )
}

fun ComicDetail.toResponse(): ComicDetailResponse {
    return ComicDetailResponse(
        id = id,
        title = title,
        subtitle = subtitle,
        coverImageUrl = coverImageUrl,
        chapterCount = chapterCount,
        pageCount = pageCount,
        tags = tags,
        updatedAt = updatedAt,
        metadata = ComicMetadataResponse(
            title = metadata.title,
            originalTitle = metadata.originalTitle,
            author = metadata.author,
            description = metadata.description,
            publisher = metadata.publisher,
            releaseYear = metadata.releaseYear,
            language = metadata.language,
            tags = metadata.tags
        ),
        latestReadingProgress = latestReadingProgress?.let {
            LatestReadingProgressResponse(
                chapterId = it.chapterId,
                chapterTitle = it.chapterTitle,
                pageIndex = it.pageIndex,
                lastReadAt = it.lastReadAt
            )
        }
    )
}

fun ChapterSummary.toResponse(): ChapterSummaryResponse {
    return ChapterSummaryResponse(
        totalChapters = totalChapters,
        completed = completed,
        lastUpdatedAt = lastUpdatedAt,
        chapters = chapters.map {
            ChapterItemResponse(
                id = it.id,
                title = it.title,
                order = it.order,
                pageCount = it.pageCount,
                updatedAt = it.updatedAt
            )
        }
    )
}

fun ChapterContent.toResponse(): ChapterContentResponse {
    return ChapterContentResponse(
        pages = pages.map {
            ChapterPageResponse(
                pageIndex = it.pageIndex,
                width = it.width,
                height = it.height,
                contentUrl = it.contentUrl
            )
        }
    )
}
