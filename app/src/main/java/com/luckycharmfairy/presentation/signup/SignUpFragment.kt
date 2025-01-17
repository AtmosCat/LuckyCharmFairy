package com.luckycharmfairy.presentation.signup

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.presentation.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentSignInBinding
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentSignUpBinding
import com.luckycharmfairy.presentation.signin.SignInFragment


class SignUpFragment : Fragment() {

    lateinit var binding: FragmentSignUpBinding

    private var doesEmailExist: User? = null

    private var emailConfirm = false
    private var pwConfirm = false
    private var privacyConfirm = false
    private var serviceTermsConfirm = false

    private var auth: FirebaseAuth? = null


    private val viewModel: UserViewModel by activityViewModels {
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
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        // 이메일 중복 체크, 빈칸 체크
//        binding.btnSignupEmailCheck.setOnClickListener {
//
//            var email = binding.etSignupEmail.text.toString()
//
//            viewModel.findUser(email)
//            viewModel.signingInUser.observe(viewLifecycleOwner) { data ->
//                doesEmailExist = data
//            }
//
//            if (email.isEmpty()) {
//                Toast.makeText(requireContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
//            } else {
//                if (doesEmailExist != null) {
//                    Toast.makeText(requireContext(), "중복된 이메일이 있습니다.", Toast.LENGTH_SHORT).show()
//                } else {
//                    emailConfirm = true
//                    Toast.makeText(requireContext(), "사용 가능한 이메일입니다.", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }

        // 비밀번호 일치 체크
        pwConfirm = false
        binding.btnSignupPwCheck.setOnClickListener {
            val pwCheck = binding.etSignupPwCheck.text.toString()
            val pw = binding.etSignupPw.text.toString()
            if (pw != pwCheck) {
                Toast.makeText(requireContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "비밀번호가 일치합니다.", Toast.LENGTH_SHORT).show()
                pwConfirm = true
            }
        }

//        binding.etSignupPw.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(
//                charSequence: CharSequence?,
//                start: Int,
//                count: Int,
//                after: Int
//            ) {
//            }
//
//            override fun onTextChanged(
//                charSequence: CharSequence?,
//                start: Int,
//                before: Int,
//                count: Int
//            ) {
//                // charSequence: 변경된 텍스트, start: 텍스트가 변경된 위치, before: 변경되기 전 텍스트의 길이, count: 변경된 문자의 수
//                pwConfirm = false
//            }
//
//            override fun afterTextChanged(editable: Editable?) {
//                val inputText = editable.toString()
//            }
//        })

        binding.checkBoxSignupPrivacy.setOnCheckedChangeListener { _, isChecked ->
            privacyConfirm = if (isChecked) {
                true
            } else {
                false
            }
        }

        binding.checkBoxSignupServiceTerms.setOnCheckedChangeListener { _, isChecked ->
            serviceTermsConfirm = if (isChecked) {
                true
            } else {
                false
            }
        }

        binding.btnSignupSignup.setOnClickListener {
            registerUser(
                binding.etSignupEmail.text.toString(),
                binding.etSignupPw.text.toString(),
                binding.etSignupName.text.toString(),
                binding.etSignupContact.text.toString(),
            )
        }

        binding.btnSignupCancel.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

    }

    // firebaseAuth 활용 회원가입
    fun registerUser(email: String, password: String, name: String, contact: String) {
        val auth = FirebaseAuth.getInstance()
        if (pwConfirm && privacyConfirm && serviceTermsConfirm) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "회원가입 성공", Toast.LENGTH_SHORT).show()
                        viewModel.addUser(
                            User(
                                email = email,
                                pw = password,
                                name = name,
                                contact = contact
                            )
                        )
                    } else {
                        val exception = task.exception
                        when (exception) {
                            is FirebaseAuthWeakPasswordException -> {
                                Toast.makeText(
                                    context,
                                    "비밀번호는 최소 6자리 이상으로 설정해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is FirebaseAuthInvalidCredentialsException -> {
                                Toast.makeText(
                                    context,
                                    "이메일 형식이 올바르지 않습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is FirebaseAuthUserCollisionException -> {
                                Toast.makeText(
                                    context,
                                    "이미 사용 중인 이메일입니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {
                                Toast.makeText(
                                    context,
                                    "회원가입 실패: ${exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
        } else if (!pwConfirm) {
            Toast.makeText(requireContext(), "비밀번호 중복확인을 해주세요.", Toast.LENGTH_SHORT).show()
        } else if (!privacyConfirm) {
            Toast.makeText(requireContext(), "개인정보 수집 및 이용 약관에 동의해주세요.", Toast.LENGTH_SHORT).show()
        } else if (!serviceTermsConfirm) {
            Toast.makeText(requireContext(), "서비스 이용 약관에 동의해주세요.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "회원가입을 진행할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, SignInFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun createAccount(
        email: String,
        pw: String,
        emailConfirm: Boolean,
        pwConfirm: Boolean,
        privacyConfirm: Boolean,
        serviceTermsConfirm: Boolean,
        isEmailFilled: Boolean,
        isPwFilled: Boolean,
        isPwCheckFilled: Boolean
    ) {
        if (!isEmailFilled || !isPwFilled || !isPwCheckFilled) {
            Toast.makeText(requireContext(), "입력되지 않은 항목이 있습니다.", Toast.LENGTH_SHORT).show()
        } else {
            if (!emailConfirm) {
                Toast.makeText(requireContext(), "이메일 중복확인을 해주세요.", Toast.LENGTH_SHORT).show()
            } else if (!pwConfirm) {
                Toast.makeText(requireContext(), "비밀번호 일치 여부를 확인해주세요.", Toast.LENGTH_SHORT)
                    .show()
            } else if (!privacyConfirm) {
                Toast.makeText(requireContext(), "개인정보 이용/수집에 동의해주세요.", Toast.LENGTH_SHORT)
                    .show()
            } else if (!serviceTermsConfirm) {
                Toast.makeText(requireContext(), "서비스 이용 약관에 동의해주세요.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val user = User(email, pw)
                viewModel.addUser(user)
                auth?.createUserWithEmailAndPassword(email, pw)
                    ?.addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                requireActivity(), "회원가입이 완료되었습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireActivity(), "회원가입에 실패했습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame, SignInFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

}