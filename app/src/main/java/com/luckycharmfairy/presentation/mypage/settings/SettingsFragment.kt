package com.luckycharmfairy.presentation.mypage.settings

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.data.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentSettingsBinding
import com.luckycharmfairy.presentation.signin.SignInFragment

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!
    private var currentUser = User();

    private val userViewModel: UserViewModel by activityViewModels {
        viewModelFactory { initializer { UserViewModel(requireActivity().application) } }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    };

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = userViewModel.currentUser.value!!

        binding.btnBack.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

        val editUserInfoFragment = requireActivity().supportFragmentManager.findFragmentByTag("EditUserInfoFragment")
        binding.btnEditUserInfo.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction().apply {
                hide(this@SettingsFragment)
                if (editUserInfoFragment == null) {
                    add(R.id.main_frame, EditUserInfoFragment(), "EditUserInfoFragment")
                } else {
                    show(editUserInfoFragment)
                }
                addToBackStack(null)
                commit()
            }
        }

        binding.btnSignOut.setOnClickListener{
            AlertDialog.Builder(requireContext())
                .setMessage("로그아웃하시겠습니까?")
                .setPositiveButton("확인") { dialog, _ ->
                    userViewModel.signOut()
                    Toast.makeText(requireContext(),"로그아웃되었습니다.",Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    val signInFragment = requireActivity().supportFragmentManager.findFragmentByTag("SignInFragment")
                    requireActivity().supportFragmentManager.beginTransaction().apply {
                        hide(this@SettingsFragment)
                        if (signInFragment == null) {
                            add(R.id.main_frame, SignInFragment(), "SignInFragment")
                        } else {
                            show(signInFragment)
                        }
                        addToBackStack(null)
                        commit()
                    }
                }
                .setNegativeButton("취소") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.btnDeleteAccount.setOnClickListener{
            AlertDialog.Builder(requireContext())
                .setTitle("회원탈퇴하시겠습니까?")
                .setMessage("계정 정보는 복구하실 수 없으며,\n개인정보는 방침상의 보관 기간이 지난 후 완전히 삭제됩니다.")
                .setPositiveButton("확인") { dialog, _ ->
                    userViewModel.deleteID(currentUser)
                    Toast.makeText(requireContext(),"회원탈퇴되었습니다.",Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    val signInFragment = requireActivity().supportFragmentManager.findFragmentByTag("SignInFragment")
                    requireActivity().supportFragmentManager.beginTransaction().apply {
                        hide(this@SettingsFragment)
                        if (signInFragment == null) {
                            add(R.id.main_frame, SignInFragment(), "SignInFragment")
                        } else {
                            show(signInFragment)
                        }
                        addToBackStack(null)
                        commit()
                    }
                }
                .setNegativeButton("취소") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }

    }

}