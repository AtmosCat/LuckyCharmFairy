package com.luckycharmfairy.presentation.mypage.myteammanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.data.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.RecyclerviewMyteamTeamsBinding

private val currentUser = User()
private val currentUserMyTeamNames = mutableListOf<String>()


class MyTeamTeamsAdapter(private val selectedSport: String, private val teams: List<String>, private val userViewModel: UserViewModel) : ListAdapter<String, MyTeamTeamsAdapter.ViewHolder>(object : DiffUtil.ItemCallback<String>() {
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
        currentUser = userViewModel.currentUser.value!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(teams[position])

        holder.btnLike.setOnClickListener {
            itemClick?.onClick(it, position)
            currentUser.myteams.add(teams[position])
        }
    }

    class ViewHolder(private val binding: RecyclerviewMyteamTeamsBinding) : RecyclerView.ViewHolder(binding.root) {
        val teamName = binding.tvTeamName
        val btnLike = binding.btnLike
        fun bind(item: String) {
            teamName.text = item
            if (currentUser.myteams.isNotEmpty()) {
                for (myteam in currentUser.myteams) {
                    if (myteam.name !in currentUserMyTeamNames) {
                        currentUserMyTeamNames += myteam.name
                    }
                }
            }
            if (item in currentUserMyTeamNames) {
                btnLike.setImageResource(R.drawable.star)
            }
        }
    }

}