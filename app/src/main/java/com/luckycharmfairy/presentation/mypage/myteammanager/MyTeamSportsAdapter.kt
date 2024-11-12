package com.luckycharmfairy.presentation.mypage.myteammanager

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.luckycharmfairy.data.model.baseballTeamNames
import com.luckycharmfairy.data.model.menBasketballTeamNames
import com.luckycharmfairy.data.model.menFootballTeamNames
import com.luckycharmfairy.data.model.menVolleyballTeamNames
import com.luckycharmfairy.data.model.womenVolleyballTeamNames
import com.luckycharmfairy.data.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.databinding.RecyclerviewMyteamSportsBinding

class MyTeamSportsAdapter : ListAdapter<String, MyTeamSportsAdapter.ViewHolder>(object : DiffUtil.ItemCallback<String>() {
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
        val binding = RecyclerviewMyteamSportsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: RecyclerviewMyteamSportsBinding) : RecyclerView.ViewHolder(binding.root) {
        val sport  = binding.tvSportTitle
        fun bind(item: String) {
            sport.text = item
            var selectedSportTeamNames = listOf<String>()
            selectedSportTeamNames = when (item) {
                "야구" -> baseballTeamNames
                "남자축구" -> menFootballTeamNames
                "남자농구" -> menBasketballTeamNames
                "남자배구" -> menVolleyballTeamNames
                "여자배구" -> womenVolleyballTeamNames
                else -> emptyList()
            }
            val teamAdapter = MyTeamTeamsAdapter(item, selectedSportTeamNames, userViewModel = UserViewModel(Application()))
            binding.recyclerviewMyteamTeams.adapter = teamAdapter
            binding.recyclerviewMyteamTeams.layoutManager = LinearLayoutManager(binding.root.context)
            val sports = selectedSportTeamNames

            sports.toMutableList().remove("직접 입력")
            teamAdapter.submitList(sports)
        }
    }
}