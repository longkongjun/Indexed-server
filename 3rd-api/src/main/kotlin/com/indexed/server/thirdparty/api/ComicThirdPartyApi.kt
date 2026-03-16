package com.indexed.server.thirdparty.api

interface ComicThirdPartyApi {
    suspend fun searchComics(query: ThirdPartySearchQuery): ThirdPartyComicPage
    suspend fun listRecommendations(): List<ThirdPartyRecommendationItem>
    suspend fun getComicDetail(comicId: String): ThirdPartyComic?
    suspend fun listComicChapters(comicId: String): List<ThirdPartyChapter>?
}
