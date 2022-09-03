package com.kis.youranimelist.ui.explore

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.ui.model.ExploreCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object ExploreScreenContract {

    data class ScreenState(
        val categories: List<AnimeCategory>,
    )

    data class AnimeCategory(
        val category: AnimeCategoryDescription,
        val pagingDataFlow: Flow<PagingData<AnimeCategoryItem>>,
    )

    data class AnimeCategoryItem(
        val id: Int,
        val pictureUrl: String?,
        val title: String,
        val year: Int?,
        val season: String?,
    )

    data class AnimeCategoryDescription(
        val tag: String,
        val title: String,
        val navigationScreen: EndlessListNavType,
    )

    sealed class EndlessListNavType {
        object SuggestionsList: EndlessListNavType()
        object RankedList: EndlessListNavType()
    }
}

fun List<Pair<ExploreCategory, Pager<Int, Anime>>>.asExploreScreenContractScreenState(
    cachedScope: CoroutineScope,
    animeCategoryMapper: (ExploreCategory) -> ExploreScreenContract.AnimeCategoryDescription = ExploreCategory::asAnimeCategoryDescription,
    animeCategoryItemMapper: (Anime) -> ExploreScreenContract.AnimeCategoryItem = Anime::asAnimeCategoryItem,
): ExploreScreenContract.ScreenState {

    val categories = this.map { pair ->
        ExploreScreenContract.AnimeCategory(
            category = animeCategoryMapper.invoke(pair.first),
            pagingDataFlow = pair.second.flow.map { pagingData ->
                pagingData.map { anime ->
                    animeCategoryItemMapper.invoke(anime)
                }
            }
                .cachedIn(cachedScope)
        )
    }

    return ExploreScreenContract.ScreenState(
        categories = categories
    )
}

fun Anime.asAnimeCategoryItem(): ExploreScreenContract.AnimeCategoryItem {
    return ExploreScreenContract.AnimeCategoryItem(
        id = this.id,
        pictureUrl = this.picture?.large,
        title = this.title,
        year = this.startSeason?.year,
        season = this.startSeason?.season,
    )
}

fun ExploreCategory.asAnimeCategoryDescription(): ExploreScreenContract.AnimeCategoryDescription {
    return when (this) {
        is ExploreCategory.Ranked -> ExploreScreenContract.AnimeCategoryDescription(
            tag = this.tag,
            title = this.presentName,
            navigationScreen = ExploreScreenContract.EndlessListNavType.RankedList,
        )
        is ExploreCategory.Suggestions -> ExploreScreenContract.AnimeCategoryDescription(
            tag = "suggestions",
            title = "Suggestions",
            navigationScreen = ExploreScreenContract.EndlessListNavType.SuggestionsList,
        )
    }
}
