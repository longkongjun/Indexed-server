package com.indexed.server.comics.api

import com.indexed.server.comics.api.dto.ErrorResponse
import com.indexed.server.comics.domain.model.ComicListQuery
import com.indexed.server.comics.domain.model.PagingQuery
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

private val supportedClients = setOf("android", "ios", "desktop", "web")
private val supportedSort = setOf("popularity", "rating", "updatedAt", "createdAt")

suspend fun ApplicationCall.requireClientOrRespond(): String? {
    val client = parameters["client"]
    if (client.isNullOrBlank() || client !in supportedClients) {
        respond(HttpStatusCode.BadRequest, ErrorResponse("client 参数非法，必须是 android/ios/desktop/web"))
        return null
    }
    return client
}

suspend fun ApplicationCall.requirePathParamOrRespond(name: String): String? {
    val value = parameters[name]
    if (value.isNullOrBlank()) {
        respond(HttpStatusCode.BadRequest, ErrorResponse("$name 不能为空"))
        return null
    }
    return value
}

suspend fun ApplicationCall.requireComicListQueryOrRespond(): ComicListQuery? {
    val page = request.queryParameters["page"]?.toIntOrNull() ?: 0
    val size = request.queryParameters["size"]?.toIntOrNull() ?: 20
    val sort = request.queryParameters["sort"] ?: "popularity"
    val keyword = request.queryParameters["keyword"]

    if (sort !in supportedSort) {
        respond(HttpStatusCode.BadRequest, ErrorResponse("sort 参数非法: $sort"))
        return null
    }

    return ComicListQuery(
        page = page.coerceAtLeast(0),
        size = size.coerceAtLeast(1),
        sort = sort,
        keyword = keyword
    )
}

fun ApplicationCall.readPagingQuery(): PagingQuery {
    val page = request.queryParameters["page"]?.toIntOrNull() ?: 0
    val size = request.queryParameters["size"]?.toIntOrNull() ?: 20
    return PagingQuery(page = page.coerceAtLeast(0), size = size.coerceAtLeast(1))
}
