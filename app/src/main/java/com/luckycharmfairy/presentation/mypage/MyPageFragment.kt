package com.luckycharmfairy.presentation.mypage

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.firebase.storage.FirebaseStorage
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.data.model.sampleBitmap
import com.luckycharmfairy.data.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentMyPageBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


class MyPageFragment : Fragment() {

    private var _binding: FragmentMyPageBinding? = null

    private val binding get() = _binding!!
    private var currentUser = User()

    private var isProfileEditting = false

    private val userViewModel: UserViewModel by activityViewModels {
        viewModelFactory { initializer { UserViewModel(requireActivity().application) } }
    }

    private lateinit var pickImageLauncher : ActivityResultLauncher<Intent>
    private var temporaryProfileBitmap = sampleBitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    val bitmap = intent?.data?.let { uri ->
                        requireActivity().contentResolver.openInputStream(uri)?.use { inputStream ->
                            BitmapFactory.decodeStream(inputStream)
                        }
                    }
                    bitmap?.let {
                        binding.ivProfileImage.setImageBitmap(it)
                    }
                    intent?.data?.let { uri ->
                        lifecycleScope.launch {
                            handleImage(uri)
                        }
                    }
                }
            }

        currentUser = userViewModel.currentUser.value!!

        if (currentUser.photo.isNotBlank()) {
            binding.ivProfileImage.load(currentUser.photo)
        } else {
            binding.ivProfileImage.setImageResource(R.drawable.basic_profile)
        }

        binding.etProfileName.setText(currentUser.nickname)

        binding.btnEditProfile.setOnClickListener{
            isProfileEditting = !isProfileEditting
            if (isProfileEditting) {
                binding.btnEditProfile.visibility = View.GONE
                binding.btnSaveProfile.visibility = View.VISIBLE
                binding.btnEditPhoto.visibility = View.VISIBLE
                binding.etProfileName.isEnabled = true
                binding.btnEditPhoto.setOnClickListener{
                    pickImage()
                }
            } else {

            }
        }

        binding.btnMypageSettings.setOnClickListener{
            val popupMenu = PopupMenu(requireContext(), binding.btnMypageSettings)
            popupMenu.menuInflater.inflate(R.menu.popup_menu_settings, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_signout -> {
                        AlertDialog.Builder(requireContext())
                            .setTitle("로그아웃")
                            .setMessage("로그아웃하시겠습니까?")
                            .setPositiveButton("확인") { dialog, _ ->
                                userViewModel.signOut()
                                Toast.makeText(requireContext(),"로그아웃되었습니다.",Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                                val signInFragment = requireActivity().supportFragmentManager.findFragmentByTag("SignInFragment")
                                requireActivity().supportFragmentManager.beginTransaction().apply {
                                    remove(this@MyPageFragment)
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
                        true
                    }
                    R.id.action_delete_id -> {
                        AlertDialog.Builder(requireContext())
                            .setTitle("회원탈퇴")
                            .setMessage("회원탈퇴하시겠습니까?\n삭제된 계정 정보는 복구하실 수 없습니다.")
                            .setPositiveButton("확인") { dialog, _ ->
                                userViewModel.deleteID(currentUser)
                                Toast.makeText(requireContext(),"회원탈퇴되었습니다.",Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                                val signInFragment = requireActivity().supportFragmentManager.findFragmentByTag("SignInFragment")
                                requireActivity().supportFragmentManager.beginTransaction().apply {
                                    remove(this@MyPageFragment)
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
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        val editProfileFragment = requireActivity().supportFragmentManager.findFragmentByTag("EditProfileFragment")
        binding.btnEditProfile.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction().apply {
                hide(this@MyPageFragment)
                if (editProfileFragment == null) {
                    add(R.id.main_frame, EditProfileFragment(), "EditProfileFragment")
                } else {
                    show(editProfileFragment)
                }
                addToBackStack(null)
                commit()
            }
        }

        userViewModel.currentUser.observe(viewLifecycleOwner) { data ->
            if (data?.photo == "") {
                binding.ivProfileImage.setImageBitmap(sampleBitmap)
            } else {
                userViewModel.getDownloadUrl(
                    onSuccess = { downloadUrl ->
                        // 이미지 로드
                        binding.ivProfileImage.load(downloadUrl) {
                            crossfade(true)
                            placeholder(R.drawable.placeholder) // 로딩 중에 표시할 이미지
                            error(R.drawable.error_image) // 로드 실패 시 표시할 이미지
                        }
                    },
                    onFailure = { exception ->
                        // 실패 처리
                        binding.ivProfileImage.setImageResource(R.drawable.error_image)
                    })
            }
            binding.tvProfileName.text = data?.nickname
            binding.tvMyAllergies.text = "⚠️ 나의 알러지 성분: ${data?.allergy}"

            val currentUserAllergiesCount = data?.allergy?.size

            if (currentUserAllergiesCount != null) {
                if (currentUserAllergiesCount >= 1) {
                    for(i in 0..currentUserAllergiesCount - 1) {
                        myAllergyFrameList[i].isVisible = true
                        myAllergyList[i].isVisible = true
                        setAllergyImage(myAllergyList[i], data.allergy[i])
                    }
                }
            }
        }

        val favoriteFragment = requireActivity().supportFragmentManager.findFragmentByTag("FavoriteFragment")
        binding.viewMypageMenu1.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction().apply {
                hide(this@MyPageFragment)
                if (favoriteFragment == null) {
                    add(R.id.main_frame, FavoriteFragment(), "FavoriteFragment")
                } else {
                    show(favoriteFragment)
                }
                addToBackStack(null)
                commit()
            }
        }

//        val membershipFragment = requireActivity().supportFragmentManager.findFragmentByTag("MembershipFragment")
//        binding.viewMypageMenu3.setOnClickListener{
//            requireActivity().supportFragmentManager.beginTransaction().apply {
//                hide(this@MyPageFragment)
//                if (membershipFragment == null) {
//                    add(R.id.main_frame, MembershipFragment(), "MembershipFragment")
//                } else {
//                    show(membershipFragment)
//                }
//                addToBackStack(null)
//                commit()
//            }
//        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    suspend fun handleImage(uri: Uri) {
        val bitmap = uriToBitmap(uri)
        bitmap?.let {
            uploadImageToFirebaseStorage(it) { imageUrl ->
                // 유저 정보 업데이트
            }
        }
    }
    private suspend fun uriToBitmap(uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            requireActivity().contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        }
    }

    private fun uploadImageToFirebaseStorage(bitmap: Bitmap, callback: (String?) -> Unit){
        val storageRef = FirebaseStorage.getInstance().reference.child("images/${System.currentTimeMillis()}.png")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()
        storageRef.putBytes(data)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }.addOnFailureListener {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }


}
