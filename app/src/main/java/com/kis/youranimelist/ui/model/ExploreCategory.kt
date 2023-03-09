package com.kis.youranimelist.ui.model

import androidx.annotation.StringRes
import com.kis.youranimelist.R

interface CachingKey {
    fun toRequest(): String
}

sealed class ExploreCategory(
    val tag: String,
) : CachingKey {
    sealed class Ranked(@StringRes val stringId: Int, tag: String) : ExploreCategory(tag) {
        object TopRanked : Ranked(R.string.explore_category_top_ranked, "all")
        object Airing : Ranked(R.string.explore_category_airing, "airing")
        object Popular : Ranked(R.string.explore_category_popular, "bypopularity")
        object Upcoming : Ranked(R.string.explore_category_upcoming, "upcoming")
        object Movies : Ranked(R.string.explore_category_movies, "movie")
        object Favorite : Ranked(R.string.explore_category_favorite, "favorite")

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
