package com.luckycharmfairy.presentation.mymatches.matchdetail

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.viewpager2.widget.ViewPager2
import com.luckycharmfairy.data.model.Match
import com.luckycharmfairy.presentation.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentMatchDetailBinding
import com.luckycharmfairy.presentation.mymatches.MyMatchesFragment
import com.luckycharmfairy.presentation.mymatches.addmatches.AddMyMatchOneFragment
import com.luckycharmfairy.presentation.mymatches.addmatches.ViewPagerAdapter
import com.luckycharmfairy.presentation.mymatches.editmatch.EditMyMatchOneFragment

private const val ARG_PARAM1 = "param1"
class MatchDetailFragment : Fragment() {
    private var param1: String? = null

    private var _binding: FragmentMatchDetailBinding? = null

    private var selectedDayMatches = mutableListOf<Match>()

    private val imageResources = mutableListOf<String>()

    private val binding get() = _binding!!

    private var clickedMatch = Match()

    private val userViewModel: UserViewModel by activityViewModels {
        viewModelFactory { initializer { UserViewModel(requireActivity().application) } }
    }

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMatchDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(param1: String) =
            MatchDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().apply {
                hide(this@MatchDetailFragment)
                add(R.id.main_frame, MyMatchesFragment())
                addToBackStack(null)
                commit()
            }
        }

        val data = param1
        selectedDayMatches = userViewModel.selectedDayMatches.value!!
        clickedMatch = selectedDayMatches.find { it.id == data }!!

        val viewPager = view.findViewById<ViewPager2>(R.id.viewpager_match_detail)

        if (clickedMatch.photos.size != 0) {
            binding.ivCamera.visibility = View.GONE
            val viewPagerAdapter = ViewPagerAdapter(clickedMatch.photos, userViewModel)
            viewPager.adapter = viewPagerAdapter
        } else {
            binding.ivCamera.visibility = View.VISIBLE
        }

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tvPhotoNumber.text = "${position + 1} / ${clickedMatch.photos.size}"
            }
        })

        binding.viewHometeam.text = clickedMatch.home.shortname
        setTeamColor(binding.viewHometeam, clickedMatch.home.teamcolor)
        binding.viewAwayteam.text = clickedMatch.away.shortname
        setTeamColor(binding.viewAwayteam, clickedMatch.away.teamcolor)
        if (clickedMatch.myteam == clickedMatch.home) {
            binding.ivHomeStar.visibility = View.VISIBLE
            binding.ivAwayStar.visibility = View.GONE
        } else if (clickedMatch.myteam == clickedMatch.away) {
            binding.ivAwayStar.visibility = View.VISIBLE
            binding.ivHomeStar.visibility = View.GONE
        } else {
            binding.ivAwayStar.visibility = View.GONE
            binding.ivHomeStar.visibility = View.GONE
        }
        binding.tvHomescore.text = clickedMatch.homescore.toString()
        binding.tvAwayscore.text = clickedMatch.awayscore.toString()
        binding.tvResult.text = clickedMatch.result
        when (clickedMatch.sport) {
            "야구" -> binding.ivSport.setImageResource(R.drawable.baseball)
            "남자축구" -> binding.ivSport.setImageResource(R.drawable.football)
            "남자농구" -> binding.ivSport.setImageResource(R.drawable.basketball)
            "남자배구" -> binding.ivSport.setImageResource(R.drawable.volleyball)
            "여자배구" -> binding.ivSport.setImageResource(R.drawable.volleyball)
            else -> binding.ivSport.visibility = View.GONE
        }

        when (clickedMatch.weather) {
            "sunny" -> binding.ivWeather.setImageResource(R.drawable.weather_sunny)
            "sunny_cloudy" -> binding.ivWeather.setImageResource(R.drawable.weather_sunny_cloudy)
            "cloudy" -> binding.ivWeather.setImageResource(R.drawable.weather_cloudy)
            "rainy" -> binding.ivWeather.setImageResource(R.drawable.weather_rainy)
            "snowy" -> binding.ivWeather.setImageResource(R.drawable.weather_snowy)
            else -> binding.ivWeather.setImageResource(R.drawable.bg_weather)
        }

        when (clickedMatch.feeling) {
            "happy" -> binding.ivFeeling.setImageResource(R.drawable.feeling_happy)
            "lovely" -> binding.ivFeeling.setImageResource(R.drawable.feeling_lovely)
            "soso" -> binding.ivFeeling.setImageResource(R.drawable.feeling_soso)
            "sad" -> binding.ivFeeling.setImageResource(R.drawable.feeling_sad)
            "angry" -> binding.ivFeeling.setImageResource(R.drawable.feeling_angry)
            else -> binding.ivFeeling.setImageResource(R.drawable.bg_weather)
        }

        binding.tvDate.text = "${clickedMatch.year}년 ${clickedMatch.month}월 ${clickedMatch.date}일(${clickedMatch.day})"
        binding.tvTime.text = clickedMatch.time
        if (clickedMatch.location.isNotBlank()) {
            binding.tvLocation.text = clickedMatch.location
        } else {
            binding.tvLocation.text = "장소 : ?"
        }
        binding.tvMvpName.text = clickedMatch.mvp
        binding.tvContent.text = clickedMatch.content

        binding.btnMenu.setOnClickListener { view ->
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.menuInflater.inflate(R.menu.popup_menu_mine, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        val dataToSend = clickedMatch.id
                        val editMatchFrag = EditMyMatchOneFragment.newInstance(dataToSend)
                        requireActivity().supportFragmentManager.beginTransaction().apply {
                            hide(this@MatchDetailFragment)
                            add(R.id.main_frame, editMatchFrag, "EditMyMatchOneFragment")
                            addToBackStack(null)
                            commit()
                        }
                        true
                    }
                    R.id.action_delete -> {
                        AlertDialog.Builder(requireContext())
                            .setTitle("직관 기록 삭제하기")
                            .setMessage("기록을 삭제하시겠습니까?")
                            .setPositiveButton("삭제") { dialog, _ ->
                                userViewModel.deleteMatch(clickedMatch.id)
//                                if (postViewModel.filteredPosts.value.isNullOrEmpty()) {
//                                    val newItems = postViewModel.allPosts.value!!.sortedBy { it.timestamp }
//                                    CommunityHomeAdapter().updateData(newItems)
//                                } else {
//                                    val newAllItems = postViewModel.allPosts.value!!.sortedBy { it.timestamp }
//                                    CommunityHomeAdapter().updateData(newAllItems)
//                                    val newFilteredItems = postViewModel.filteredPosts.value!!.sortedBy { it.timestamp }
//                                    PostListAdapter().updateData(newFilteredItems)
//                                }
                                Toast.makeText(requireContext(), "직관 기록이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                            .setNegativeButton("취소") { dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
            popupMenu.show()
        }


    }

    private fun setTeamColor(team: Button, teamcolor: String) {
        team.setBackgroundColor(Color.parseColor(teamcolor))
    }

}

