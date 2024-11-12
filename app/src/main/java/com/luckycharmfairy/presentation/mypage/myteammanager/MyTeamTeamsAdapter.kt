package com.luckycharmfairy.presentation.mypage.myteammanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.luckycharmfairy.data.model.Team
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.data.model.allTeams
import com.luckycharmfairy.data.model.baseballTeams
import com.luckycharmfairy.data.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.RecyclerviewMyteamTeamsBinding

private var currentUser = User()
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

    init {
        currentUser = userViewModel.currentUser.value!!
//        userViewModel.currentUser.observeForever { data ->
//            if (data != null) {
//                currentUser = data
//            }
//        }
    }

    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerviewMyteamTeamsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(teams[position])

        holder.btnLike.setOnClickListener {
            itemClick?.onClick(it, position)
//            var selectedTeam = Team()
//            if (selectedSport == "야구"
//                || selectedSport == "남자축구"
//                || selectedSport == "남자농구"
//                || selectedSport == "남자배구"
//                || selectedSport == "여자배구") {
//                selectedTeam = baseballTeams.find { it.name == teams[position] }!!
//            } else {
//                selectedTeam = currentUser.myteams.find { it.name == teams[position] }!!
//            }
//            if (selectedTeam !in currentUser.myteams) {
//                userViewModel.currentUser.value!!.myteams.add(selectedTeam)
//                userViewModel.updateCurrentUserInfo()
//                holder.btnLike.setImageResource(R.drawable.star)
//            } else {
//                currentUser.myteams.remove(selectedTeam)
//                userViewModel.updateWholeCurrentUserInfo(currentUser)
//                holder.btnLike.setImageResource(R.drawable.empty_star)
//            }
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