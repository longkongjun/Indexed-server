package com.indexed.server.comics.domain.repository

import com.indexed.server.comics.domain.model.ChapterContent
import com.indexed.server.comics.domain.model.ChapterSummary
import com.indexed.server.comics.domain.model.ComicDetail
import com.indexed.server.comics.domain.model.ComicListQuery
import com.indexed.server.comics.domain.model.ComicPage
import com.indexed.server.comics.domain.model.PagingQuery
import com.indexed.server.comics.domain.model.ReadingHistoryPage
import com.indexed.server.comics.domain.model.RecommendSection

interface ComicCatalogRepository {
    suspend fun listComics(client: String, query: ComicListQuery): ComicPage
    suspend fun listRecommended(client: String): List<RecommendSection>
    suspend fun listRecent(client: String, query: PagingQuery): ComicPage
    suspend fun listReadingHistory(query: PagingQuery): ReadingHistoryPage
    suspend fun getComicDetail(client: String, comicId: String): ComicDetail?
    suspend fun getChapterSummary(comicId: String): ChapterSummary?
    suspend fun getChapterContent(chapterId: String): ChapterContent?
}
