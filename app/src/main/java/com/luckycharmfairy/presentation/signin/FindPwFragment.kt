package com.luckycharmfairy.presentation.signin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.luckycharmfairy.presentation.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentFindIdBinding
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentFindPwBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FindPwFragment : Fragment() {

    lateinit var binding: FragmentFindPwBinding

    private var auth: FirebaseAuth? = null
    private val db = FirebaseFirestore.getInstance();

    private val userviewModel: UserViewModel by activityViewModels() {
        viewModelFactory { initializer { UserViewModel(requireActivity().application) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        auth!!.setLanguageCode("ko")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindPwBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnFindPw.setOnClickListener {
            val name = binding.etFindPwName.text.toString()
            val email = binding.etFindPwEmail.text.toString()
            lifecycleScope.launch {
                if (findUser(name, email)) {
                    sendPasswordResetEmail(email) { _, message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "일치하는 유저 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun findUser(name: String, email: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            db.collection("user")
                .whereEqualTo("email", email)
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        continuation.resume(true)
                    } else {
                        continuation.resume(false)
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }

    private fun sendPasswordResetEmail(email: String, onResult: (Boolean, String) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, "가입하신 이메일 주소로 비밀번호 재설정을 위한 링크가 전송되었습니다.")
                } else {
                    onResult(false, task.exception?.message ?: "이메일 전송에 실패했습니다.")
                }
            }
    }
}