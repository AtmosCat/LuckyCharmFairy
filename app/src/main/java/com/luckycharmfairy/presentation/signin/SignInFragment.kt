package com.luckycharmfairy.presentation.signin

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.presentation.viewmodel.UserViewModel
import com.luckycharmfairy.presentation.mymatches.MyMatchesFragment
import com.luckycharmfairy.presentation.signup.SignUpFragment
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentMyMatchesBinding
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentSignInBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignInFragment : Fragment() {

    lateinit var binding : FragmentSignInBinding

    private var user: User? = null
    private var _users: MutableList<User>? = null

    private var auth : FirebaseAuth? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    private val db = FirebaseFirestore.getInstance()

    // ActivityResultLauncher를 사용하여 구글 로그인 인텐트를 시작하고 결과를 처리
    // 결과가 성공적이면 handleSignInResult(data)를 호출하여 로그인 결과를 처리
    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        Log.d("LOGIN--", task.toString())
        try {
            val account = task.getResult(ApiException::class.java)!!
            Log.d("LOGIN--22", account.idToken!!)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google 로그인 실패
            Toast.makeText(requireContext(), "Google 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }

    }

    private val userviewModel: UserViewModel by activityViewModels() {
        viewModelFactory { initializer { UserViewModel(requireActivity().application) } }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        // 사용자의 아이디 정보와 기본 프로필을 요청
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestServerAuthCode(getString(R.string.google_login_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(),gso)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignin.setOnClickListener {
            val email = binding.etSigninEmail.text.toString()
            val pw = binding.etSigninPw.text.toString()

            userviewModel.findUser(email)
            userviewModel.signingInUser.observe(viewLifecycleOwner) { data ->
                user = data
                if (binding.etSigninEmail.text.isEmpty() || binding.etSigninPw.text.isEmpty()) {
                    Toast.makeText(requireContext(), "입력되지 않은 항목이 있습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    if (user == null) {
                        Toast.makeText(requireContext(), "존재하지 않는 이메일입니다.", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        if (pw != user!!.pw) {
                            Toast.makeText(requireContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            CoroutineScope(Dispatchers.Main).launch {
                                userviewModel.setCurrentUser("dd@gmail.com")
                            }
                            Toast.makeText(requireContext(), "로그인 성공!", Toast.LENGTH_SHORT).show()

                            val franchiseHomeFragment = requireActivity().supportFragmentManager.findFragmentByTag("FranchiseHomeFragment")
                            requireActivity().supportFragmentManager.beginTransaction().apply {
                                hide(this@SignInFragment)
                                if (franchiseHomeFragment == null) {
                                    add(R.id.main_frame, MyMatchesFragment(), "MyMatchesFragment")
                                } else {
                                    show(franchiseHomeFragment)
                                }
                                addToBackStack(null)
                                commit()
                            }
                        }
                    }
                }
            }
        }

        binding.btnSignup.setOnClickListener{
            Toast.makeText(requireContext(), "회원가입 페이지로 이동합니다.", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame, SignUpFragment())
                .addToBackStack(null)
                .commit()
        }

//        binding.btnSigninFindIdPw.setOnClickListener{
//            AlertDialog.Builder(requireContext())
//                .setTitle("이메일/비밀번호 찾기")
//                .setMessage("항목을 선택해주세요.")
//                .setPositiveButton("비밀번호 찾기") { dialog, which ->
//                    requireActivity().supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_frame, FindPwFragment())
//                        .addToBackStack(null)
//                        .commit()
//                }
//                .setNegativeButton("이메일 찾기") { dialog, which ->
//                    requireActivity().supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_frame, FindIdFragment())
//                        .addToBackStack(null)
//                        .commit()
//                }
//                .setNeutralButton("닫기") { dialog, which ->
//                }
//                .show()
//        }

        binding.btnGoogleSignin.setOnClickListener{
            googleLogin()
        }
    }

    // 구글 로그인 인텐트를 생성하고 googleSignInLauncher를 사용하여 인텐트를 실행
    private fun googleLogin() {
        val signInIntent = googleSignInClient.signInIntent
        signInIntent.let {
            googleSignInLauncher.launch(it)
        } ?: Toast.makeText(requireContext(), "구글 로그인 인텐트 생성 실패", Toast.LENGTH_SHORT).show()
    }

    // 설명: 구글 로그인 계정으로부터 받은 토큰을 사용하여 Firebase 인증을 시도
    fun firebaseAuthWithGoogle(idToken: String){
        var credential = GoogleAuthProvider.getCredential(idToken,null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener{
                    task ->
                if(task.isSuccessful) {
                    val googleLoginUser = auth!!.currentUser
                    saveGoogleLoginToFireStore(googleLoginUser!!)

                }else{
                    // 틀렸을 때
                    Toast.makeText(requireContext(),task.exception?.message,Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun saveGoogleLoginToFireStore(user: FirebaseUser) {
        val email = user.email
        userviewModel.findUser(email!!)
        userviewModel.signingInUser.observe(viewLifecycleOwner) { data ->
            if (data == null) {
                userviewModel.addUser(User(email = email.toString()))
                CoroutineScope(Dispatchers.Main).launch {
                    userviewModel.setCurrentUser(email.toString())
                }
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    hide(this@SignInFragment)
                    add(R.id.main_frame, MyMatchesFragment(), "MyMatchesFragment")
                    addToBackStack(null)
                    commit()
                }
                Toast.makeText(requireContext(),"구글 계정으로 로그인합니다.",Toast.LENGTH_SHORT).show()
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    userviewModel.setCurrentUser(email.toString())
                }
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    hide(this@SignInFragment)
                    add(R.id.main_frame, MyMatchesFragment(), "MyMatchesFragment")
                    addToBackStack(null)
                    commit()
                }
                Toast.makeText(requireContext(),"구글 계정으로 로그인합니다.",Toast.LENGTH_SHORT).show()
            }
        }
    }
}