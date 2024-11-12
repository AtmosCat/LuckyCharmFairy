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
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.data.viewmodel.UserViewModel
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

    private val myTeamSportsAdapter by lazy { MyTeamSportsAdapter() }

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

        binding.recyclerviewMyteam.adapter = myTeamSportsAdapter
        binding.recyclerviewMyteam.layoutManager = LinearLayoutManager(requireContext())

//        val recyclerviewMyTeams = requireActivity().findViewById<RecyclerView>(R.id.recyclerview_myteam_teams)
//        recyclerviewMyTeams.adapter = myTeamTeamsAdapter
//        recyclerviewMyTeams.layoutManager = LinearLayoutManager(requireContext())

        currentUser = userViewModel.currentUser.value!!

        binding.btnBack.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

        val sports = currentUser.mysports
        sports.removeAt(0)
        myTeamSportsAdapter.submitList(sports)
//        val currentUserMyTeamNames = mutableListOf<String>()
//        for (myteam in currentUser.myteams) {
//            if (myteam.name !in currentUserMyTeamNames) {
//                currentUserMyTeamNames += myteam.name
//            }
//        }




    }

}