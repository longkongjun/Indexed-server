package com.indexed.server

import com.indexed.server.api.configureRouting
import com.indexed.server.api.configureTemplateRouting
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import io.ktor.server.thymeleaf.Thymeleaf
import kotlinx.serialization.json.Json
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        })
    }

    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, _ ->
//            call.respondRedirect("/content/404.html")
            val html = javaClass.classLoader.getResourceAsStream("mycontent/404.html")?.reader()?.readText()
                ?: "<h1>404 Not Found</h1>"
            call.respondText(
                text = html,
                status = HttpStatusCode.NotFound,
                contentType = ContentType.Text.Html
            )
        }

        exception<IllegalStateException> { call, cause ->
            call.respondText("App in illegal state as ${cause.message}")
        }
    }

//    configureRouting()
    configureTemplateRouting()
}
