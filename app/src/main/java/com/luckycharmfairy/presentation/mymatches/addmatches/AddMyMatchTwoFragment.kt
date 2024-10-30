package com.luckycharmfairy.presentation.mymatches.addmatches

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.luckycharmfairy.R
import com.luckycharmfairy.data.viewmodel.UserViewModel
import com.luckycharmfairy.databinding.FragmentAddMyMatchOneBinding
import com.luckycharmfairy.databinding.FragmentAddMyMatchTwoBinding

class AddMyMatchTwoFragment : Fragment() {

    private var _binding: FragmentAddMyMatchTwoBinding? = null
    private val binding get() = _binding!!

    private var currentUserEmail: String = ""

    private val userViewModel: UserViewModel by activityViewModels {
        viewModelFactory { initializer { UserViewModel(requireActivity().application) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddMyMatchTwoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


}