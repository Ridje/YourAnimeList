package com.kis.youranimelist.ui.explore

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kis.youranimelist.R
import com.kis.youranimelist.databinding.ExploreFragmentGroupBinding
import com.kis.youranimelist.model.Anime

class ExploreAdapter(private val animeGroups:List<Map<String, Any>>) : RecyclerView.Adapter<ExploreAnimeGroup>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreAnimeGroup {
        val binding = ExploreFragmentGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExploreAnimeGroup(binding);
    }

    override fun onBindViewHolder(holder: ExploreAnimeGroup, position: Int) {
        holder.bind(animeGroups[position])
    }

    override fun getItemCount(): Int = animeGroups.size
}

class ExploreAnimeGroup(val binding: ExploreFragmentGroupBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(groupInfo:Map<String, Any>) {
        binding.groupName.text = groupInfo["name"] as String
        binding.itemList.adapter = ExploreItemsAdapter(groupInfo["items"] as List<Anime>)
        binding.itemList.addItemDecoration(
            SimpleDividerItemDecorationLastExcluded(
                binding.itemList.resources.getDimension(R.dimen.itemsMargin)
            )
        )
    }
}

class SimpleDividerItemDecorationLastExcluded(private val spacing: Float) :
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