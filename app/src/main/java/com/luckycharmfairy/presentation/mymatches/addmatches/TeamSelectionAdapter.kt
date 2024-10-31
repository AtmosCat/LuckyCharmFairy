package com.luckycharmfairy.presentation.mymatches.addmatches

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.luckycharmfairy.databinding.RecyclerviewTeamSelectionBinding

class TeamSelectionAdapter : ListAdapter<String, TeamSelectionAdapter.ViewHolder>(object : DiffUtil.ItemCallback<String>() {
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
        val binding = RecyclerviewTeamSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.itemView.setOnClickListener {
            itemClick?.onClick(it, position)
        }
    }

    class ViewHolder(private val binding: RecyclerviewTeamSelectionBinding) : RecyclerView.ViewHolder(binding.root) {
        var brand = binding.tvTeamName
        fun bind(item: String) {
            brand.text = item
        }
    }
}
