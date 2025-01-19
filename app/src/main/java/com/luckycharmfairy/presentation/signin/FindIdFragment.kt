package com.luckycharmfairy.presentation.signin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.luckycharmfairy.presentation.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentFindIdBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FindIdFragment : Fragment() {

    lateinit var binding: FragmentFindIdBinding

    private var auth: FirebaseAuth? = null
    private val db = FirebaseFirestore.getInstance();

    private val userviewModel: UserViewModel by activityViewModels() {
        viewModelFactory { initializer { UserViewModel(requireActivity().application) } }
    };

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindIdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFindId.setOnClickListener {
            val name = binding.etFindIdName.text.toString()
            val contact = binding.etFindIdContact.text.toString()
            lifecycleScope.launch {
                val result = findUserEmail(name, contact)
                binding.tvResult.visibility = View.VISIBLE
                binding.tvResult.text = "확인 결과: $result"
            }
        }
    }

    private suspend fun findUserEmail(name: String, contact: String): String =
        suspendCancellableCoroutine { continuation ->
            db.collection("user")
                .whereEqualTo("contact", contact)
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val email = documents.first().getString("email").toString()
                        continuation.resume(email) // 작업 성공 시 결과 반환
                    } else {
                        continuation.resume("일치하는 유저 정보가 없습니다.") // 결과가 없을 때
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception) // 작업 실패 시 예외 처리
                }
        }
}