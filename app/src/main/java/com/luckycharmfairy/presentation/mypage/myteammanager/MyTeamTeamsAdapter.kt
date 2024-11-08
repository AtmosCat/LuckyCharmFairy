package com.luckycharmfairy.presentation.mypage.myteammanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.luckycharmfairy.luckycharmfairy.databinding.RecyclerviewMyteamTeamsBinding

class MyTeamTeamsAdapter : ListAdapter<String, MyTeamTeamsAdapter.ViewHolder>(object : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}) {

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerviewMyteamTeamsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.btnLike.setOnClickListener {
            itemClick?.onClick(it, position)
        }
    }

    class ViewHolder(private val binding: RecyclerviewMyteamTeamsBinding) : RecyclerView.ViewHolder(binding.root) {
        val teamName = binding.tvTeamName
        val btnLike = binding.btnLike
        fun bind(item: String) {
            teamName.text = item
        }
    }
}