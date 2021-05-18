package com.kis.youranimelist.repository

import com.kis.youranimelist.model.Anime
import com.kis.youranimelist.model.AnimeCategory

object RepositoryMock : Repository {
    override fun getAnimeListByCategory() : List<AnimeCategory> {

        val animeByCategory = ArrayList<AnimeCategory>();
        animeByCategory.add(
            AnimeCategory(
                "On air", listOf(
                    Anime(
                        42361,
                        "Ijiranaide, Nagatoro-san",
                        null,
                        2021,
                        7.16f
                    ), Anime(
                        47639,
                        "Blue Reflection Ray",
                        null,
                        2021,
                        5.56f
                    ), Anime(
                        41587,
                        "Boku no Hero Academia 5th Season",
                        null,
                        2021,
                        7.52f
                    ), Anime(
                        41025,
                        "Fumetsu no Anata e",
                        null,
                        2021,
                        8.66f
                    ), Anime(
                        40938,
                        "Hige wo Soru. Soshite Joshikousei wo Hirou.",
                        null,
                        2021,
                        7.81f
                    ), Anime(
                        42249,
                        "Tokyo Revengers",
                        null,
                        2021,
                        8.06f
                    ), Anime(
                        41623,
                        "Isekai Maou to Shoukan Shoujo no Dorei Majutsu Ω",
                        null,
                        2021,
                        7.02f
                    ), Anime(
                        41457,
                        "86",
                        null,
                        2021,
                        7.95f
                    )
                )
            )
        )

        animeByCategory.add(
            AnimeCategory(
                "Top", listOf(
                    Anime(
                        5114,
                        "Fullmetal Alchemist: Brotherhood",
                        null,
                        2009,
                        9.17f
                    ), Anime(
                        38524,
                        "Shingeki no Kyojin Season 3 Part 2",
                        null,
                        2019,
                        9.11f
                    ), Anime(
                        9253,
                        "Steins;Gate",
                        null,
                        2019,
                        9.11f
                    ), Anime(
                        28977,
                        "Gintama°",
                        null,
                        2019,
                        9.09f
                    ), Anime(
                        11061,
                        "Hunter x Hunter (2011)",
                        null,
                        2019,
                        9.08f
                    )
                )
            )
        )

        animeByCategory.add(
            AnimeCategory("Suggestions",
                listOf(
                    Anime(
                        5114,
                        "Fullmetal Alchemist: Brotherhood",
                        null,
                        2009,
                        9.17f)
                    , Anime(
                        38524,
                        "Shingeki no Kyojin Season 3 Part 2",
                        null,
                        2019,
                        9.11f)
                    , Anime(
                        9253,
                        "Steins;Gate",
                        null,
                        2019,
                        9.11f)
                    , Anime(
                        28977,
                        "Gintama°",
                        null,
                        2019,
                        9.09f)
                    , Anime(
                        11061,
                        "Hunter x Hunter (2011)",
                        null,
                        2019,
                        9.08f)
                )
            )
        )

        return animeByCategory
    }

    override fun getAnimeInfo(): Anime {
        return Anime(
            5114,
            "Fullmetal Alchemist: Brotherhood",
            null,
            2009,
            9.17f)
    }
}