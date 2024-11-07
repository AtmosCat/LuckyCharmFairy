package com.luckycharmfairy.presentation.mymatches.mysports

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.luckycharmfairy.luckycharmfairy.databinding.RecyclerviewMySportsBinding

class MySportsAdapter : ListAdapter<String, MySportsAdapter.ViewHolder>(object : DiffUtil.ItemCallback<String>() {
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
        val binding = RecyclerviewMySportsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.btnDelete.setOnClickListener {
            itemClick?.onClick(it, position)
        }
    }

    class ViewHolder(private val binding: RecyclerviewMySportsBinding) : RecyclerView.ViewHolder(binding.root) {
        var sport = binding.tvSportName
        var btnDelete = binding.btnDeleteMySport
        fun bind(item: String) {
            sport.text = item
        }
    }

    fun updateData(){
        notifyDataSetChanged()
    }
}
