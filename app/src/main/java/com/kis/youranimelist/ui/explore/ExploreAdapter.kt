package com.kis.youranimelist.ui.explore

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kis.youranimelist.R
import com.kis.youranimelist.databinding.ExploreFragmentGroupBinding
import com.kis.youranimelist.model.AnimeCategory

class ExploreAdapter(private val animeCategories:List<AnimeCategory>, private val itemClickListener:ExploreItemsAdapter.OnItemClickListener?) : RecyclerView.Adapter<ExploreAdapter.ExploreAnimeCategory>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreAnimeCategory {
        val binding = ExploreFragmentGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExploreAnimeCategory(binding);
    }

    override fun onBindViewHolder(holder: ExploreAnimeCategory, position: Int) {
        holder.bind(animeCategories[position])
    }

    override fun getItemCount(): Int = animeCategories.size

    inner class ExploreAnimeCategory(private val binding: ExploreFragmentGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(animeCategory: AnimeCategory) {
            binding.apply {
                categoryName.text = animeCategory.name
                itemList.adapter = ExploreItemsAdapter(animeCategory.animeList, itemClickListener)
                itemList.addItemDecoration(
                    SimpleDividerItemDecorationLastExcluded(
                        itemList.resources.getDimension(R.dimen.itemsMargin)))
            }
        }
    }

    inner class SimpleDividerItemDecorationLastExcluded(private val spacing: Float) :
        RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            rect: Rect,
            view: View,
            parent: RecyclerView,
            s: RecyclerView.State
        ) {
            parent.adapter?.let { adapter ->
                rect.right = when (parent.getChildAdapterPosition(view)) {
                    RecyclerView.NO_POSITION, adapter.itemCount - 1 -> 0
                    else -> spacing.toInt()
                }
            }
        }

    }

}

