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
import com.luckycharmfairy.presentation.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentMyMatchesBinding
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentMyPageBinding
import com.luckycharmfairy.presentation.mymatches.MyMatchesFragment
import com.luckycharmfairy.presentation.mymatches.mysports.MySportsFragment
import com.luckycharmfairy.presentation.mypage.myteammanager.MyTeamManagerFragment
import com.luckycharmfairy.presentation.mypage.settings.SettingsFragment
import com.luckycharmfairy.utils.FragmentUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


class MyPageFragment : Fragment() {

    lateinit var binding : FragmentMyPageBinding

    private var currentUser = User()

    private var isProfileEditting = false

    private val userViewModel: UserViewModel by activityViewModels {
        viewModelFactory { initializer { UserViewModel(requireActivity().application) } }
    }

    private lateinit var pickImageLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
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

        currentUser = userViewModel.getCurrentUser()!!

        if (currentUser.photo.isNotBlank()) {
            binding.ivProfileImage.load(currentUser.photo)
        } else {
            binding.ivProfileImage.setImageResource(R.drawable.basic_profile)
        }

        binding.etProfileName.setText(currentUser.nickname)

        binding.btnEditProfile.setOnClickListener{
            isProfileEditting = true
            binding.btnEditProfile.visibility = View.GONE
            binding.btnSaveProfile.visibility = View.VISIBLE
            binding.btnEditPhoto.visibility = View.VISIBLE
            binding.etProfileName.isEnabled = true
            binding.btnEditPhoto.setOnClickListener{
                pickImage()
            }
        }

        binding.btnSaveProfile.setOnClickListener{
            isProfileEditting = false
            userViewModel.currentUser.value!!.nickname = binding.etProfileName.text.toString()
            userViewModel.updateCurrentUserInfo()
            binding.btnEditProfile.visibility = View.VISIBLE
            binding.btnSaveProfile.visibility = View.GONE
            binding.btnEditPhoto.visibility = View.GONE
            binding.etProfileName.isEnabled = false
        }

        binding.btnProfile.setOnClickListener{
            // 내 프로필 이동
        }

        binding.btnTabMyMatches.setOnClickListener {
            FragmentUtils.hideAndShowFragment(
                requireActivity().supportFragmentManager,
                this@MyPageFragment,
                MyMatchesFragment(),
                "MyMatchesFragment"
            )
        }

        binding.btnMysportsManager.setOnClickListener{
            FragmentUtils.hideAndShowFragment(
                requireActivity().supportFragmentManager,
                this@MyPageFragment,
                MySportsFragment(),
                "MySportsFragment"
            )
        }

        binding.btnSettings.setOnClickListener{
            FragmentUtils.hideAndShowFragment(
                requireActivity().supportFragmentManager,
                this@MyPageFragment,
                SettingsFragment(),
                "SettingsFragment"
            )
        }

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
                if (imageUrl != null) {
                    userViewModel.currentUser.value?.photo = imageUrl
                }
                userViewModel.updateCurrentUserInfo()
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
