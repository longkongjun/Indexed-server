package com.indexed.server.api

import com.indexed.server.api.dto.ErrorBody
import com.indexed.server.api.dto.LibraryEntry
import com.indexed.server.api.dto.LibraryListResponse
import com.indexed.server.config.SourceConfig
import com.indexed.server.storage.ListSourceResult
import com.indexed.server.storage.listSourceRoot
import com.indexed.server.task.Priority
import com.indexed.server.task.Task
import com.indexed.server.task.TaskRepository
import com.indexed.server.task.tasksAsTable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val sourceConfig = SourceConfig.from(environment.config)



    routing {
        staticResources("/content", "mycontent")
        staticResources("/task-ui", "task-ui")

        get("/content") {
            call.respondRedirect("/content/index.html")
        }

        get("/") {
            call.respondText("Hello World!")
        }
        get("/test1") {
            val text = "<h1>Hello From Ktor</h1>"
            val type = ContentType.parse("text/html")
            call.respondText(text, type)
        }

        get("/error-test") {
            throw IllegalStateException("Too Busy")
        }

        get("/config/source") {
            when (val result = listSourceRoot(sourceConfig.root)) {
                is ListSourceResult.Ok -> call.respond(
                    LibraryListResponse(
                        root = result.root,
                        entries = result.entries.map { LibraryEntry(it.name, it.isDirectory) }
                    )
                )

                is ListSourceResult.NotFound -> call.respond(
                    HttpStatusCode.NotFound,
                    ErrorBody(error = "资源来源目录不存在", path = result.path)
                )

                is ListSourceResult.NotDirectory -> call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorBody(error = "资源来源路径不是目录", path = result.path)
                )
            }
        }

        route("/tasks") {
            get {
                val taskList = TaskRepository.allTasks()
                call.respondText(
                    contentType = ContentType.parse("text/html"),
                    text = taskList.tasksAsTable()
                )
            }

            get("/priority/{priority?}") {
                val priorityAsText = call.parameters["priority"]
                if (priorityAsText == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val priority = Priority.entries.find { it.name == priorityAsText }
                if (priority == null) {
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    val taskList = TaskRepository.tasksByPriority(priority)
                    if (taskList.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                    } else {
                        call.respondText(
                            contentType = ContentType.parse("text/html"),
                            text = taskList.tasksAsTable()
                        )
                    }
                }

            }

            get("/byName/{taskName}") {
                val name = call.parameters["taskName"]
                if (name == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                val task = TaskRepository.taskByName(name)
                if (task == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }

                call.respondText(
                    contentType = ContentType.parse("text/html"),
                    text = listOf(task).tasksAsTable()
                )
            }

            get("/byPriority/{priority?}") {
                //Code remains the same
            }


            post {
                val formContent = call.receiveParameters()
                val name = formContent["name"] ?: ""
                val description = formContent["description"] ?: ""
                val priorityText = formContent["priority"] ?: ""
                if (name.isEmpty() || description.isEmpty() || priorityText.isEmpty()) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                try {
                    val priority = Priority.valueOf(priorityText)
                    TaskRepository.addTask(Task(name, description, priority))
                    call.respond(HttpStatusCode.NoContent)
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}
