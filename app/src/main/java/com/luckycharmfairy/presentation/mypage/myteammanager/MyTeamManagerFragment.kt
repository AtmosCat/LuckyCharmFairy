package com.luckycharmfairy.presentation.mypage.myteammanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luckycharmfairy.data.model.Team
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.data.model.baseballTeamNames
import com.luckycharmfairy.data.model.baseballTeams
import com.luckycharmfairy.data.model.menBasketballTeamNames
import com.luckycharmfairy.data.model.menFootballTeamNames
import com.luckycharmfairy.data.model.menVolleyballTeamNames
import com.luckycharmfairy.data.model.menVolleyball_AnsanOkbankOkman
import com.luckycharmfairy.data.model.womenVolleyballTeamNames
import com.luckycharmfairy.presentation.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentMyTeamManagerBinding
import com.luckycharmfairy.presentation.mymatches.MyMatchesAdapter
import com.luckycharmfairy.presentation.mymatches.matchdetail.MatchDetailFragment

class MyTeamManagerFragment : Fragment() {

    private var _binding: FragmentMyTeamManagerBinding? = null

    private val binding get() = _binding!!
    private var currentUser = User()

    private val userViewModel: UserViewModel by activityViewModels {
        viewModelFactory { initializer { UserViewModel(requireActivity().application) } }
    }

    private val selectedSportTeamNames = mutableListOf<String>()
    private var selectedSport = ""

    private val myTeamSportsAdapter by lazy { MyTeamSportsAdapter() }
    private val myTeamTeamsAdapter by lazy { MyTeamTeamsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyTeamManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerviewMyteamSports.adapter = myTeamSportsAdapter
        binding.recyclerviewMyteamSports.layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerviewMyteamTeams.adapter = myTeamTeamsAdapter
        binding.recyclerviewMyteamTeams.layoutManager = LinearLayoutManager(requireContext())

        currentUser = userViewModel.currentUser.value!!

        binding.btnBack.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

        val mySports = currentUser.mysports
        mySports[0] = "⭐MY팀" // 전체 종목 제거
        myTeamSportsAdapter.submitList(mySports)
//        val currentUserMyTeamNames = mutableListOf<String>()
//        for (myteam in currentUser.myteams) {
//            if (myteam.name !in currentUserMyTeamNames) {
//                currentUserMyTeamNames += myteam.name
//            }
//        }

        myTeamSportsAdapter.itemClick = object : MyTeamSportsAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                selectedSport = mySports[position]
                when (selectedSport) {
                    "야구" -> myTeamTeamsAdapter.submitList(baseballTeamNames)
                    "남자축구" -> myTeamTeamsAdapter.submitList(menFootballTeamNames)
                    "남자농구" -> myTeamTeamsAdapter.submitList(menBasketballTeamNames)
                    "남자배구" -> myTeamTeamsAdapter.submitList(menVolleyballTeamNames)
                    "여자배구" -> myTeamTeamsAdapter.submitList(womenVolleyballTeamNames)
                    else -> myTeamTeamsAdapter
                }


            }
        }



    }

}