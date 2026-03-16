package com.indexed.server.thirdparty.api

data class ThirdPartySearchQuery(
    val page: Int,
    val size: Int,
    val keyword: String?,
    val orderBy: String
)

data class ThirdPartyComicPage(
    val items: List<ThirdPartyComic>,
    val page: Int,
    val size: Int,
    val total: Int
)

data class ThirdPartyComic(
    val id: String,
    val title: String?,
    val titleEnglish: String?,
    val titleOriginal: String?,
    val synopsis: String?,
    val chapterCount: Int?,
    val score: Double?,
    val tags: List<String>,
    val coverImageUrl: String?,
    val publishedFrom: String?,
    val publishedTo: String?,
    val authors: List<String>,
    val publisher: String?,
    val status: String?,
    val contentUrl: String?
)

data class ThirdPartyRecommendationItem(
    val id: String,
    val title: String?,
    val coverImageUrl: String?
)

data class ThirdPartyChapter(
    val chapterNumber: Int,
    val title: String?,
    val updatedAt: String?,
    val contentUrl: String?
)
