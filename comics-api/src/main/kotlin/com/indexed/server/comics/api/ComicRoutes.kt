package com.indexed.server.comics.api

import com.indexed.server.comics.model.ErrorResponse
import com.indexed.server.comics.service.ComicsService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureComicRoutes(service: ComicsService = ComicsService()) {
    routing {
        route("/{client}") {
            get("/comics") {
                val client = call.requireClientOrRespond() ?: return@get
                val query = call.requireComicListQueryOrRespond() ?: return@get
                call.respond(service.listComics(client, query.page, query.size, query.sort, query.keyword))
            }

            get("/comics/recommended") {
                val client = call.requireClientOrRespond() ?: return@get
                call.respond(service.listRecommended(client))
            }

            get("/comics/recent") {
                val client = call.requireClientOrRespond() ?: return@get
                val paging = call.readPagingQuery()
                call.respond(service.listRecent(client, paging.page, paging.size))
            }

            get("/reading-history") {
                val client = call.requireClientOrRespond() ?: return@get
                val paging = call.readPagingQuery()
                call.respond(service.listReadingHistory(client, paging.page, paging.size))
            }

            get("/comics/detail/{comicId}") {
                val client = call.requireClientOrRespond() ?: return@get
                val comicId = call.requirePathParamOrRespond("comicId") ?: return@get

                val detail = service.getComicDetail(client, comicId)
                if (detail == null) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("漫画不存在: $comicId"))
                    return@get
                }
                call.respond(detail)
            }

            get("/comics/detail/{comicId}/chapters") {
                call.requireClientOrRespond() ?: return@get
                val comicId = call.requirePathParamOrRespond("comicId") ?: return@get

                val summary = service.getChapterSummary(comicId)
                if (summary == null) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("漫画不存在: $comicId"))
                    return@get
                }
                call.respond(summary)
            }

            get("/chapters/{chapterId}/content") {
                val client = call.requireClientOrRespond() ?: return@get
                val chapterId = call.requirePathParamOrRespond("chapterId") ?: return@get

                val content = service.getChapterContent(client, chapterId)
                if (content == null) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("章节不存在: $chapterId"))
                    return@get
                }
                call.respond(content)
            }
        }
    }
}
