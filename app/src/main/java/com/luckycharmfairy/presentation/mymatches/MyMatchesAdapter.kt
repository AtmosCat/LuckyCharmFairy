package com.luckycharmfairy.presentation.mymatches

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.luckycharmfairy.data.model.Match
import com.luckycharmfairy.data.viewmodel.UserViewModel
import com.luckycharmfairy.databinding.RecyclerviewMatchesBinding

class MyMatchesAdapter(private val userViewModel: UserViewModel) :
    ListAdapter<Match, MyMatchesAdapter.HomeViewHolder>(object :
        DiffUtil.ItemCallback<Match>() {
        // 구 값, 신 값 비교해서 바뀐 것들만 업데이트
        override fun areItemsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem == newItem
        }
    }) {

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick : ItemClick? = null

    // RecyclerView 돌아갈 때 새로운 뷰 홀더 생성 및 초기화
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding =
            RecyclerviewMatchesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    // 홀더에 실제 데이터 할당
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        runCatching {
            holder.bind(getItem(position))
            holder.itemView.setOnClickListener {
                itemClick?.onClick(it, position)
            }
        }.onFailure { exception ->
            Log.e("MyMatchesAdapter", "Exception! ${exception.message}")
        }
    }

    class HomeViewHolder(binding: RecyclerviewMatchesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val timeAndLocation = binding.tvMatchTimeAndLocation
        val hometeam = binding.viewHometeam
        val awayteam = binding.viewAwayteam
        val homescore = binding.tvHomescore
        val awayscore = binding.tvAwayscore
        val result = binding.tvResult
        val homestar = binding.ivHomeStar
        val awaystar = binding.ivAwayStar

        fun bind(item: Match) {
            timeAndLocation.text = "${item.date} ${item.time} - ${item.location}"
            hometeam.text = item.home
            awayteam.text = item.away
            homescore.text = item.homescore.toString()
            awayscore.text = item.awayscore.toString()
            if (item.myteam == item.home) {
                homestar.visibility = View.VISIBLE
                awaystar.visibility = View.GONE
            } else {
                awaystar.visibility = View.VISIBLE
                homestar.visibility = View.GONE
            }
            if (item.myteam == item.home) {
                if (item.homescore > item.awayscore) {
                    result.text = "WIN"
                } else if (item.homescore < item.awayscore) {
                    result.text = "LOSE"
                } else {
                    result.text = "TIE"
                }
            }
            if (item.myteam == item.away) {
                if (item.homescore > item.awayscore) {
                    result.text = "LOSE"
                } else if (item.homescore < item.awayscore) {
                    result.text = "WIN"
                } else {
                    result.text = "TIE"
                }
            }

        }
    }
}
