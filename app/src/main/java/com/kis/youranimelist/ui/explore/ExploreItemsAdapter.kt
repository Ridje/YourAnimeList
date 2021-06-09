package com.kis.youranimelist.ui.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kis.youranimelist.R
import com.kis.youranimelist.databinding.ExploreFragmentItemBinding
import com.kis.youranimelist.model.app.Anime
import com.squareup.picasso.Picasso

class ExploreItemsAdapter(val animeList:List<Anime>, val clickListener: ((Anime)-> Unit)) : RecyclerView.Adapter<ExploreItemsAdapter.ExploreAnimeItem>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreAnimeItem {
        val binding = ExploreFragmentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExploreAnimeItem(binding);
    }

    override fun onBindViewHolder(holder: ExploreAnimeItem, position: Int) {
        holder.bind(animeList[position])
    }

    override fun getItemCount(): Int = animeList.size

    inner class ExploreAnimeItem(val binding : ExploreFragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(anime: Anime) {
            binding.itemTitle.text = anime.title
            Picasso.get().load(anime.mainPicture?.medium).error(R.drawable.default_image).into(binding.itemImage)
            binding.root.setOnClickListener { clickListener.invoke(animeList[adapterPosition]) }
        }
    }

}

