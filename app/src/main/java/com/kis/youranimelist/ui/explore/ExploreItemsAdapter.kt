package com.kis.youranimelist.ui.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kis.youranimelist.R
import com.kis.youranimelist.databinding.ExploreFragmentItemBinding
import com.kis.youranimelist.model.Anime
import com.kis.youranimelist.model.ranking_response.AnimeRankingItem
import com.squareup.picasso.Picasso

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
            Picasso.get().load(anime.mainPicture?.medium).error(R.drawable.default_image).into(binding.itemImage)
            binding.root.setOnClickListener { itemClickListener?.onItemClickListener(animeList[adapterPosition]) }
        }
    }

    interface OnItemClickListener {
        fun onItemClickListener(anime : Anime)
    }
}

