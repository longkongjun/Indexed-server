package com.indexed.server.comics.domain.model

data class ComicListQuery(
    val page: Int,
    val size: Int,
    val sort: String,
    val keyword: String?
)

data class PagingQuery(
    val page: Int,
    val size: Int
)
