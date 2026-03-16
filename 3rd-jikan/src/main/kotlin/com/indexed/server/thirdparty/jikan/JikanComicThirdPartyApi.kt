package com.indexed.server.thirdparty.jikan

import com.indexed.server.thirdparty.api.ComicThirdPartyApi
import com.indexed.server.thirdparty.api.ThirdPartyChapter
import com.indexed.server.thirdparty.api.ThirdPartyComic
import com.indexed.server.thirdparty.api.ThirdPartyComicPage
import com.indexed.server.thirdparty.api.ThirdPartyRecommendationItem
import com.indexed.server.thirdparty.api.ThirdPartySearchQuery
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class JikanComicThirdPartyApi(
    private val client: HttpClient = defaultHttpClient()
) : ComicThirdPartyApi {

    override suspend fun searchComics(query: ThirdPartySearchQuery): ThirdPartyComicPage {
        val response = client.get("$BASE_URL/manga") {
            parameter("page", query.page.coerceAtLeast(0) + 1)
            parameter("limit", query.size.coerceIn(1, 25))
            parameter("order_by", query.orderBy)
            parameter("sort", "desc")
            parameter("sfw", true)
            if (!query.keyword.isNullOrBlank()) parameter("q", query.keyword)
        }
        if (!response.status.isSuccess()) {
            throw IllegalStateException("Jikan manga 查询失败: ${response.status.value}")
        }
        val result = response.body<JikanListResponse<JikanManga>>()
        return ThirdPartyComicPage(
            items = result.data.map { it.toThirdPartyComic() },
            page = query.page,
            size = query.size,
            total = result.pagination?.items?.total ?: result.data.size
        )
    }

    override suspend fun listRecommendations(): List<ThirdPartyRecommendationItem> {
        val response = client.get("$BASE_URL/recommendations/manga") {
            parameter("sfw", true)
        }
        if (!response.status.isSuccess()) {
            throw IllegalStateException("Jikan 推荐查询失败: ${response.status.value}")
        }
        return response.body<JikanListResponse<JikanRecommendationBlock>>().data
            .flatMap { it.entry }
            .distinctBy { it.malId }
            .map {
                ThirdPartyRecommendationItem(
                    id = it.malId.toString(),
                    title = it.title,
                    coverImageUrl = it.images?.jpg?.largeImageUrl ?: it.images?.jpg?.imageUrl
                )
            }
    }

    override suspend fun getComicDetail(comicId: String): ThirdPartyComic? {
        val malId = comicId.toIntOrNull() ?: return null
        val response = client.get("$BASE_URL/manga/$malId/full")
        if (response.status.value == 404) return null
        if (!response.status.isSuccess()) {
            throw IllegalStateException("Jikan manga 详情查询失败: ${response.status.value}")
        }
        return response.body<JikanSingleResponse<JikanManga>>().data.toThirdPartyComic()
    }

    override suspend fun listComicChapters(comicId: String): List<ThirdPartyChapter>? {
        val malId = comicId.toIntOrNull() ?: return null
        var currentPage = 1
        val all = mutableListOf<JikanChapter>()
        while (currentPage <= 20) {
            val response = client.get("$BASE_URL/manga/$malId/chapters") {
                parameter("page", currentPage)
            }
            if (response.status.value == 404) return null
            if (!response.status.isSuccess()) {
                throw IllegalStateException("Jikan 章节列表查询失败: ${response.status.value}")
            }
            val result = response.body<JikanListResponse<JikanChapter>>()
            all += result.data
            if (result.pagination?.hasNextPage != true) break
            currentPage++
        }
        return all.mapNotNull { chapter ->
            val number = chapter.chapter?.toIntOrNull() ?: return@mapNotNull null
            ThirdPartyChapter(
                chapterNumber = number,
                title = chapter.title,
                updatedAt = chapter.date,
                contentUrl = chapter.url
            )
        }
    }

    private fun JikanManga.toThirdPartyComic(): ThirdPartyComic {
        val tags = (genres + themes + demographics).mapNotNull { it.name }.distinct().take(5)
        return ThirdPartyComic(
            id = malId.toString(),
            title = title,
            titleEnglish = titleEnglish,
            titleOriginal = titleJapanese,
            synopsis = synopsis,
            chapterCount = chapters,
            score = score,
            tags = tags,
            coverImageUrl = images?.jpg?.largeImageUrl ?: images?.jpg?.imageUrl,
            publishedFrom = published?.from,
            publishedTo = published?.to,
            authors = authors.mapNotNull { it.name },
            publisher = serializations.firstOrNull()?.name,
            status = status,
            contentUrl = url
        )
    }

    private companion object {
        private const val BASE_URL = "https://api.jikan.moe/v4"

        private fun defaultHttpClient(): HttpClient {
            return HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            explicitNulls = false
                        }
                    )
                }
                defaultRequest {
                    header(HttpHeaders.UserAgent, "IndexedServer/1.0")
                }
            }
        }
    }
}
