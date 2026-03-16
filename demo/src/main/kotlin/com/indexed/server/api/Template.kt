package com.indexed.server.api

import com.indexed.server.task.Priority
import com.indexed.server.task.Task
import com.indexed.server.task.TaskRepository
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.server.thymeleaf.ThymeleafContent
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

fun Application.configureTemplateRouting() {

    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/thymeleaf/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }

    routing {
//        get("/html-thymeleaf") {
//            call.respond(
//                ThymeleafContent(
//                    "index",
//                    mapOf("user" to ThymeleafUser(1, "user1"))
//                )
//            )
//        }
        //this is the additional route to add
        get("/tasks") {
            call.respond(ThymeleafContent("all-tasks", mapOf("tasks" to TaskRepository.allTasks())))
        }
    }
}