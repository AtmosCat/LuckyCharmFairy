package com.luckycharmfairy.presentation.mymatches.matchdetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.luckycharmfairy.data.model.Match
import com.luckycharmfairy.data.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentMatchDetailBinding

private const val ARG_PARAM1 = "param1"
class MatchDetailFragment : Fragment() {
    private var param1: String? = null

    private var _binding: FragmentMatchDetailBinding? = null
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

        val data = param1
        sharedViewModel.filteredFoods.observe(viewLifecycleOwner) { filteredFoods ->
            clickedItem = filteredFoods.find { it.prdlstReportNo == data }!!

            binding.btnBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }

            val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)

            val imageResources =
                listOf(clickedItem?.imgurl1.toString(), clickedItem?.imgurl2.toString())

            val viewPagerAdapter = ViewPagerAdapter(imageResources)
            viewPager.adapter = viewPagerAdapter

            sharedViewModel.getMarketDetail(clickedItem?.manufacture.toString(), clickedItem?.prdlstNm.toString())
        }
    }


}

