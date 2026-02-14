package com.indexed.server.task

enum class Priority {
    Low, Medium, High, Vital
}

data class Task(
    val name: String,
    val description: String,
    val priority: Priority
)

fun Task.taskAsRow() = """
    <tr><td>$name</td><td>$description</td><td>$priority</td></tr>
""".trimIndent()

fun List<Task>.tasksAsTable() = this.joinToString(
    prefix = "<table><tr><th>Name</th><th>Description</th><th>Priority</th></tr>\n",
    postfix = "\n</table>",
    separator = "\n",
    transform = Task::taskAsRow
)