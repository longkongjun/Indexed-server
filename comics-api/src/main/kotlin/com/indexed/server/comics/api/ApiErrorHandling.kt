package com.indexed.server.comics.api

import com.indexed.server.comics.api.dto.ErrorResponse
import com.indexed.server.comics.application.UseCaseResult
import com.indexed.server.comics.domain.model.ComicError
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

suspend inline fun <T> ApplicationCall.respondUseCaseResult(
    result: UseCaseResult<T>,
    success: suspend (T) -> Unit
) {
    when (result) {
        is UseCaseResult.Ok -> success(result.value)
        is UseCaseResult.Err -> respondComicError(result.error)
    }
}

suspend fun ApplicationCall.respondComicError(error: ComicError) {
    when (error) {
        is ComicError.Validation -> respond(HttpStatusCode.BadRequest, ErrorResponse(error.message))
        is ComicError.NotFound -> respond(HttpStatusCode.NotFound, ErrorResponse(error.message))
        is ComicError.UpstreamUnavailable -> respond(HttpStatusCode.BadGateway, ErrorResponse(error.message))
    }
}
