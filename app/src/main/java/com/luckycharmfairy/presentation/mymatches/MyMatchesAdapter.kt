package com.luckycharmfairy.presentation.mymatches

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.luckycharmfairy.data.model.Match
import com.luckycharmfairy.data.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.RecyclerviewMatchesBinding

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
        val sport = binding.ivSport

        fun bind(item: Match) {
            timeAndLocation.text = "${item.year}년 ${item.month}월 ${item.date}일 (${item.day}) - ${item.location}"
            hometeam.text = item.home.shortname
            setTeamColor(hometeam, item.home.teamcolor)
            awayteam.text = item.away.shortname
            setTeamColor(awayteam, item.away.teamcolor)
            homescore.text = item.homescore.toString()
            awayscore.text = item.awayscore.toString()
            result.text = item.result
            if (item.myteam == "홈 팀") {
                homestar.visibility = View.VISIBLE
                awaystar.visibility = View.GONE
            } else if (item.myteam == "어웨이 팀") {
                awaystar.visibility = View.VISIBLE
                homestar.visibility = View.GONE
            } else {
                awaystar.visibility = View.GONE
                homestar.visibility = View.GONE
            }
            when (item.sport) {
                "야구" -> sport.setImageResource(R.drawable.baseball)
                "남자축구" -> sport.setImageResource(R.drawable.football)
                "남자농구" -> sport.setImageResource(R.drawable.basketball)
                "남자배구" -> sport.setImageResource(R.drawable.volleyball)
                "여자배구" -> sport.setImageResource(R.drawable.volleyball)
                else -> sport.visibility = View.GONE
            }
        }
        private fun setTeamColor(team: Button, teamcolor: String) {
            team.setBackgroundColor(Color.parseColor(teamcolor))
        }

    }
}
