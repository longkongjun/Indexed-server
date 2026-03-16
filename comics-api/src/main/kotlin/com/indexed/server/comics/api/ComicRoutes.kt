package com.indexed.server.comics.api

import com.indexed.server.comics.application.ComicsUseCases
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureComicRoutes() {
    val useCases: ComicsUseCases by dependencies

    routing {
        route("/{client}") {
            get("/comics") {
                val client = call.requireClientOrRespond() ?: return@get
                val query = call.requireComicListQueryOrRespond() ?: return@get
                val result = useCases.listComics(client, query)
                call.respondUseCaseResult(result) { call.respond(it.toResponse()) }
            }

            get("/comics/recommended") {
                val client = call.requireClientOrRespond() ?: return@get
                val result = useCases.listRecommended(client)
                call.respondUseCaseResult(result) { call.respond(it.toResponse()) }
            }

            get("/comics/recent") {
                val client = call.requireClientOrRespond() ?: return@get
                val paging = call.readPagingQuery()
                val result = useCases.listRecent(client, paging)
                call.respondUseCaseResult(result) { call.respond(it.toResponse()) }
            }

            get("/reading-history") {
                call.requireClientOrRespond() ?: return@get
                val paging = call.readPagingQuery()
                val result = useCases.listReadingHistory(paging)
                call.respondUseCaseResult(result) { call.respond(it.toResponse()) }
            }

            get("/comics/detail/{comicId}") {
                val client = call.requireClientOrRespond() ?: return@get
                val comicId = call.requirePathParamOrRespond("comicId") ?: return@get
                val result = useCases.getComicDetail(client, comicId)
                call.respondUseCaseResult(result) { call.respond(it.toResponse()) }
            }

            get("/comics/detail/{comicId}/chapters") {
                call.requireClientOrRespond() ?: return@get
                val comicId = call.requirePathParamOrRespond("comicId") ?: return@get
                val result = useCases.getChapterSummary(comicId)
                call.respondUseCaseResult(result) { call.respond(it.toResponse()) }
            }

            get("/chapters/{chapterId}/content") {
                call.requireClientOrRespond() ?: return@get
                val chapterId = call.requirePathParamOrRespond("chapterId") ?: return@get
                val result = useCases.getChapterContent(chapterId)
                call.respondUseCaseResult(result) { call.respond(it.toResponse()) }
            }
        }
    }
}
