package com.kis.youranimelist.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kis.youranimelist.databinding.ExploreFragmentGroupBinding
import com.kis.youranimelist.databinding.HistoryFragmentItemBinding
import com.kis.youranimelist.model.app.AnimeViewHistory
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(private val animeViewsHistory:List<AnimeViewHistory>): RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.HistoryViewHolder{
        val binding = HistoryFragmentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding);
    }

    override fun onBindViewHolder(holder: HistoryAdapter.HistoryViewHolder, position: Int) {
        holder.bind(animeViewsHistory[position])
    }

    override fun getItemCount(): Int {
        return animeViewsHistory.size
    }

    inner class HistoryViewHolder(private val binding: HistoryFragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewHistoryItem: AnimeViewHistory) {
            binding.historyCreatedAt.text = dateFormat.format(viewHistoryItem.clickDate)
            binding.historyItemAnimeName.text = viewHistoryItem.animeName
        }
    }
}