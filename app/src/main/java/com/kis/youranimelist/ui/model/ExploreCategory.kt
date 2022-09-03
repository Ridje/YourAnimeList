package com.kis.youranimelist.ui.model

interface CachingKey {
    fun toRequest(): String
}

sealed class ExploreCategory(
    val tag: String,
) : CachingKey {
    sealed class Ranked(val presentName: String, tag: String) : ExploreCategory(tag) {
        object TopRanked : Ranked("Top ranked", "all")
        object Airing : Ranked("Airing", "airing")
        object Popular : Ranked("Popular", "bypopularity")
        object Upcoming : Ranked("Upcoming", "upcoming")
        object Movies : Ranked("Movies", "movie")
        object Favorite : Ranked("Favorite", "favorite")

        object Factory {
            fun getByTag(tag: String): Ranked {
                return when (tag) {
                    TopRanked.tag -> TopRanked
                    Airing.tag -> Airing
                    Popular.tag -> Popular
                    Upcoming.tag -> Upcoming
                    Movies.tag -> Movies
                    Favorite.tag -> Favorite

                    else -> throw WrongExploreCategoryTag(tag)
                }
            }
        }
    }

    // request tag is not important for suggestions category
    object Suggestions : ExploreCategory("suggestions")

    override fun toRequest(): String {
        return tag
    }
}

class WrongExploreCategoryTag(tag: String) :
    Exception("Tag $tag is not registered in system, can't call this factory with not registered tag")
