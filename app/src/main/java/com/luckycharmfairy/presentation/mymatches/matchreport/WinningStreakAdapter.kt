package com.luckycharmfairy.presentation.mymatches.matchreport

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.luckycharmfairy.data.model.Match
import com.luckycharmfairy.luckycharmfairy.databinding.RecyclerviewWinningStreakBinding

class WinningStreakAdapter : ListAdapter<Match, WinningStreakAdapter.ViewHolder>(object : DiffUtil.ItemCallback<Match>() {
    override fun areItemsTheSame(oldItem: Match, newItem: Match): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Match, newItem: Match): Boolean {
        return oldItem == newItem
    }
}) {

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerviewWinningStreakBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: RecyclerviewWinningStreakBinding) : RecyclerView.ViewHolder(binding.root) {
        var match = binding.tvMatch
        fun bind(item: Match) {
            match.text = "$${item.year}.${item.month}.${item.date}(${item.day}) / ${item.location} / ${item.home.shortname} vs ${item.away.shortname} / ${item.homescore}:${item.awayscore} ${item.result} "
        }
    }

    fun updateData(){
        notifyDataSetChanged()
    }
}
