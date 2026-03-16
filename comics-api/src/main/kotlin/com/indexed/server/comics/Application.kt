package com.indexed.server.comics

import com.indexed.server.comics.api.configureComicRoutes
import com.indexed.server.comics.api.dto.ErrorResponse
import com.indexed.server.comics.application.buildComicsUseCases
import com.indexed.server.comics.infrastructure.repository.ThirdPartyComicCatalogRepository
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.plugins.di.provide
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import com.indexed.server.thirdparty.api.ComicThirdPartyApi
import com.indexed.server.thirdparty.jikan.JikanComicThirdPartyApi
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                explicitNulls = false
            }
        )
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("服务内部错误: ${cause.message}"))
        }
    }

    dependencies {
        provide<ComicThirdPartyApi> { JikanComicThirdPartyApi() }
        provide { ThirdPartyComicCatalogRepository(resolve()) }
        provide { buildComicsUseCases(resolve()) }
    }

    configureComicRoutes()
}
