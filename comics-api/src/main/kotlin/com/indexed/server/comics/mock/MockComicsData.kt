package com.indexed.server.comics.mock

data class MockComic(
    val id: String,
    val title: String,
    val subtitle: String,
    val coverPageId: String,
    val chapterCount: Int,
    val pageCount: Int,
    val rating: Double,
    val tags: List<String>,
    val updatedAt: String,
    val createdAt: String,
    val metadata: MockMetadata,
    val completed: Boolean,
    val chapters: List<MockChapter>
)

data class MockMetadata(
    val title: String,
    val originalTitle: String,
    val author: String,
    val description: String,
    val publisher: String,
    val releaseYear: Int,
    val language: String,
    val tags: List<String>
)

data class MockChapter(
    val id: String,
    val title: String,
    val order: Int,
    val pageCount: Int,
    val updatedAt: String,
    val pages: List<MockPage>
)

data class MockPage(
    val pageId: String,
    val width: Int,
    val height: Int
)

data class MockReadingHistory(
    val comicId: String,
    val chapterId: String,
    val pageIndex: Int,
    val lastReadAt: String
)

data class MockRecommendGroup(
    val title: String,
    val subtitle: String,
    val style: String,
    val comicIds: List<String>
)

object MockComicsData {
    val comics: List<MockComic> = listOf(
        MockComic(
            id = "comic-1",
            title = "海贼王",
            subtitle = "ONE PIECE",
            coverPageId = "page-cover-1",
            chapterCount = 1100,
            pageCount = 18320,
            rating = 9.2,
            tags = listOf("热血", "冒险"),
            updatedAt = "2026-03-14T12:00:00Z",
            createdAt = "1997-07-22T00:00:00Z",
            metadata = MockMetadata(
                title = "海贼王",
                originalTitle = "ONE PIECE",
                author = "尾田荣一郎",
                description = "伟大航路的冒险故事",
                publisher = "集英社",
                releaseYear = 1997,
                language = "ja",
                tags = listOf("热血", "冒险")
            ),
            completed = false,
            chapters = listOf(
                MockChapter(
                    id = "chapter-1001",
                    title = "第1001话",
                    order = 1001,
                    pageCount = 18,
                    updatedAt = "2026-03-14T12:00:00Z",
                    pages = (0 until 18).map {
                        MockPage(pageId = "comic-1-chapter-1001-page-$it", width = 1600, height = 2400)
                    }
                ),
                MockChapter(
                    id = "chapter-1000",
                    title = "第1000话",
                    order = 1000,
                    pageCount = 19,
                    updatedAt = "2026-02-27T09:10:00Z",
                    pages = (0 until 19).map {
                        MockPage(pageId = "comic-1-chapter-1000-page-$it", width = 1600, height = 2400)
                    }
                )
            )
        ),
        MockComic(
            id = "comic-2",
            title = "咒术回战",
            subtitle = "呪術廻戦",
            coverPageId = "page-cover-2",
            chapterCount = 270,
            pageCount = 4630,
            rating = 8.8,
            tags = listOf("战斗", "校园"),
            updatedAt = "2026-03-12T08:30:00Z",
            createdAt = "2018-03-05T00:00:00Z",
            metadata = MockMetadata(
                title = "咒术回战",
                originalTitle = "呪術廻戦",
                author = "芥见下下",
                description = "诅咒与咒术师的对抗故事",
                publisher = "集英社",
                releaseYear = 2018,
                language = "ja",
                tags = listOf("战斗", "校园")
            ),
            completed = true,
            chapters = listOf(
                MockChapter(
                    id = "chapter-270",
                    title = "第270话",
                    order = 270,
                    pageCount = 21,
                    updatedAt = "2026-03-12T08:30:00Z",
                    pages = (0 until 21).map {
                        MockPage(pageId = "comic-2-chapter-270-page-$it", width = 1600, height = 2400)
                    }
                ),
                MockChapter(
                    id = "chapter-269",
                    title = "第269话",
                    order = 269,
                    pageCount = 20,
                    updatedAt = "2026-03-05T08:30:00Z",
                    pages = (0 until 20).map {
                        MockPage(pageId = "comic-2-chapter-269-page-$it", width = 1600, height = 2400)
                    }
                )
            )
        ),
        MockComic(
            id = "comic-3",
            title = "葬送的芙莉莲",
            subtitle = "Frieren",
            coverPageId = "page-cover-3",
            chapterCount = 135,
            pageCount = 2250,
            rating = 9.0,
            tags = listOf("奇幻", "治愈"),
            updatedAt = "2026-03-10T06:20:00Z",
            createdAt = "2020-04-28T00:00:00Z",
            metadata = MockMetadata(
                title = "葬送的芙莉莲",
                originalTitle = "葬送のフリーレン",
                author = "山田钟人 / 阿部司",
                description = "勇者队伍解散后，精灵魔法使的旅程",
                publisher = "小学馆",
                releaseYear = 2020,
                language = "ja",
                tags = listOf("奇幻", "治愈")
            ),
            completed = false,
            chapters = listOf(
                MockChapter(
                    id = "chapter-135",
                    title = "第135话",
                    order = 135,
                    pageCount = 17,
                    updatedAt = "2026-03-10T06:20:00Z",
                    pages = (0 until 17).map {
                        MockPage(pageId = "comic-3-chapter-135-page-$it", width = 1500, height = 2200)
                    }
                ),
                MockChapter(
                    id = "chapter-134",
                    title = "第134话",
                    order = 134,
                    pageCount = 18,
                    updatedAt = "2026-02-25T06:20:00Z",
                    pages = (0 until 18).map {
                        MockPage(pageId = "comic-3-chapter-134-page-$it", width = 1500, height = 2200)
                    }
                )
            )
        )
    )

    val readingHistory: List<MockReadingHistory> = listOf(
        MockReadingHistory(
            comicId = "comic-1",
            chapterId = "chapter-1001",
            pageIndex = 7,
            lastReadAt = "2026-03-14T11:50:00Z"
        ),
        MockReadingHistory(
            comicId = "comic-3",
            chapterId = "chapter-135",
            pageIndex = 3,
            lastReadAt = "2026-03-13T20:12:00Z"
        ),
        MockReadingHistory(
            comicId = "comic-2",
            chapterId = "chapter-270",
            pageIndex = 12,
            lastReadAt = "2026-03-11T09:00:00Z"
        )
    )

    val recommendedGroups: List<MockRecommendGroup> = listOf(
        MockRecommendGroup(
            title = "热血推荐",
            subtitle = "燃到停不下来",
            style = "horizontal_scroll",
            comicIds = listOf("comic-1", "comic-2")
        ),
        MockRecommendGroup(
            title = "新近入库",
            subtitle = "刚刚上架的新漫画",
            style = "grid",
            comicIds = listOf("comic-3", "comic-2")
        ),
        MockRecommendGroup(
            title = "主编精选",
            subtitle = "编辑本周推荐",
            style = "banner",
            comicIds = listOf("comic-1")
        )
    )
}
