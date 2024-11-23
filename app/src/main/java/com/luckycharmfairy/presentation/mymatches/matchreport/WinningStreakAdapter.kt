package com.luckycharmfairy.presentation.mymatches.matchreport

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.luckycharmfairy.data.model.Match
import com.luckycharmfairy.luckycharmfairy.R
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
            // 전체 텍스트를 하나의 문자열로 만들기
            val fullText = "· ${item.year}.${item.month}.${item.date}(${item.day}) / ${item.location} / ${item.home.shortname} vs ${item.away.shortname} / ${item.homescore}:${item.awayscore} ${item.result}"

            // SpannableString을 사용하여 텍스트의 일부분을 변경
            val spannableString = SpannableString(fullText)

            // home.shortname 부분을 볼드로 스타일 적용
            var start = 0
            var end = 0
            if (item.myteam == item.home) {
                start = fullText.indexOf(item.home.shortname)
                end = start + item.home.shortname.length
            } else if (item.myteam == item.away) {
                start = fullText.indexOf(item.away.shortname)
                end = start + item.away.shortname.length
            }
            spannableString.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(itemView.context, R.color.main_mint)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            // 스타일이 적용된 텍스트를 TextView에 설정
            match.text = spannableString
        }
    }

    fun updateData(){
        notifyDataSetChanged()
    }
}
