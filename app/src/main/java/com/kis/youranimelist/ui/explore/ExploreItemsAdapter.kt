package com.kis.youranimelist.ui.explore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kis.youranimelist.databinding.ExploreFragmentItemBinding
import com.kis.youranimelist.model.Anime

class ExploreItemsAdapter(val animeList:List<Anime>, private val itemClickListener: OnItemClickListener?) : RecyclerView.Adapter<ExploreItemsAdapter.ExploreAnimeItem>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreAnimeItem {
        val binding = ExploreFragmentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExploreAnimeItem(binding);
    }

    override fun onBindViewHolder(holder: ExploreAnimeItem, position: Int) {
        holder.bind(animeList[position])
    }

    override fun getItemCount(): Int = animeList.size

    inner class ExploreAnimeItem(val binding : ExploreFragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(anime:Anime) {
            binding.itemTitle.text = anime.title
            binding.root.setOnClickListener { itemClickListener?.onItemClickListener(animeList[adapterPosition]) }
        }
    }

    interface OnItemClickListener {
        fun onItemClickListener(anime : Anime)
    }
}

