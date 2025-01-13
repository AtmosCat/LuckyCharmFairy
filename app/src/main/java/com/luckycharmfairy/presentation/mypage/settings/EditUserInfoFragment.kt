package com.luckycharmfairy.presentation.mypage.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.presentation.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentEditUserInfoBinding

class EditUserInfoFragment : Fragment() {

    private var _binding: FragmentEditUserInfoBinding? = null

    private val binding get() = _binding!!
    private var currentUser = User()

    private var pwConfirm = false
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
        _binding = FragmentEditUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = userViewModel.currentUser.value!!

        binding.btnBack.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.etEmail.setText("${currentUser.email}")
        binding.etPw.setText("${currentUser.pw}")

        pwConfirm = false
        binding.btnPwCheck.setOnClickListener {
            val pwCheck = binding.etPwCheck.text.toString()
            val pw = binding.etPw.text.toString()
            if (pw != pwCheck) {
                Toast.makeText(requireContext(), "비밀번호가 일치하기 않습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "비밀번호가 일치합니다.", Toast.LENGTH_SHORT).show()
                pwConfirm = true
            }
        }

        binding.etPw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // charSequence: 변경된 텍스트, start: 텍스트가 변경된 위치, before: 변경되기 전 텍스트의 길이, count: 변경된 문자의 수
                pwConfirm = false
            }

            override fun afterTextChanged(editable: Editable?) {
                val inputText = editable.toString()
            }
        })

        binding.btnSave.setOnClickListener {
            if (pwConfirm) {
                val pw = binding.etPw.text.toString()
                currentUser.pw = pw
                userViewModel.currentUser.value!!.pw = pw
                userViewModel.updateCurrentUserInfo()
                Toast.makeText(requireContext(), "회원 정보가 수정되었습니다.", Toast.LENGTH_SHORT)
                    .show()
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "비밀번호 일치 여부를 확인해주세요.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}