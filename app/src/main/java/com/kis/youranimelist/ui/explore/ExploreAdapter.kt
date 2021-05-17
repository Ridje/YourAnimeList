package com.kis.youranimelist.ui.explore

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kis.youranimelist.model.Anime

class ExploreAdapter(private val animeGroups:List<String>) : RecyclerView.Adapter<ExploreAnimeGroup>() {

    private val groups: MutableMap<String, List<Anime>>

    init {
        groups = HashMap()
        for (animeGroup in animeGroups) {
            groups[animeGroup] = ArrayList()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreAnimeGroup {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ExploreAnimeGroup, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int = animeGroups.size
}

class ExploreAnimeGroup(val itemView: View) : RecyclerView.ViewHolder(itemView) {

}