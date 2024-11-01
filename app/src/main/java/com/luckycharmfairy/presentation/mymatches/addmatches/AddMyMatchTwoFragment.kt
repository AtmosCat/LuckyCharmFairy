package com.luckycharmfairy.presentation.mymatches.addmatches

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.storage.FirebaseStorage
import com.luckycharmfairy.R
import com.luckycharmfairy.data.viewmodel.UserViewModel
import com.luckycharmfairy.databinding.FragmentAddMyMatchTwoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class AddMyMatchTwoFragment : Fragment() {

    private var _binding: FragmentAddMyMatchTwoBinding? = null
    private val binding get() = _binding!!

    private var currentUserEmail: String = ""

    private var matchContent: String = ""

    private val userViewModel: UserViewModel by activityViewModels {
        viewModelFactory { initializer { UserViewModel(requireActivity().application) } }
    }

    private val imageResources = mutableListOf<String>()

    private lateinit var pickImageLauncher : ActivityResultLauncher<Intent>

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

        binding.btnClose.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

        // 갤러리에서 이미지 선택
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val uris= mutableListOf<Uri>()

                    data?.let {
                        if (it.clipData != null) {
                            val clipData = it.clipData
                            for (i in 0 until clipData!!.itemCount) {
                                uris.add(clipData.getItemAt(i).uri)
                            }
                        } else {
                            it.data?.let { uri -> uris.add(uri) }
                        }
                        lifecycleScope.launch {
                            handleImages(uris)
                        }
                    }
                }
            }

        binding.btnAddPhoto.setOnClickListener{
            Toast.makeText(this.requireContext(), "올리실 사진을 선택해주세요.", Toast.LENGTH_SHORT).show()
            pickImages()
        }

        binding.viewPagerContentPhoto.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tvPhotoCount.text = "${position + 1} / ${imageResources.size}"
            }
        })

        binding.btnSave.setOnClickListener {
            userViewModel.addNewMatch(matchContent)
            Toast.makeText(requireContext(), "직관 기록이 저장되었습니다.", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.beginTransaction().apply {
                remove(this@AddMyMatchTwoFragment)
                addToBackStack(null)
                commit()
            }
        }


    }
    // 1. 갤러리에서 복수 이미지 선택할 수 있는 인텐트 생성 및 팝업
    private fun pickImages() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        pickImageLauncher.launch(intent)
    }

    // 선택한 이미지에서 URI를 추출해 저장
    suspend fun handleImages(uris: List<Uri>) {
        for (uri in uris) {
            val bitmap = uriToBitmap(uri)
            bitmap?.let {
                uploadImageToFirebaseStorage(it) { imageUrl ->
                    imageUrl?.let { url ->
                        imageResources.add(url)
                        userViewModel.saveTemporaryImageUrl(url)
                        // 모든 이미지가 처리된 후에 ViewPager를 업데이트
                        if (uris.indexOf(uri) == uris.size - 1) {
                            val photoAdapter = ViewPagerAdapter(imageResources, userViewModel)
                            binding.viewPagerContentPhoto.adapter = photoAdapter
                        }
                        if (imageResources.size == 0) {
                            binding.btnAddPhoto.visibility = View.VISIBLE
                        } else {
                            binding.btnAddPhoto.visibility = View.GONE
                        }
                    }
                }
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
    // Bitmap 파일을 Firebase Storage에 저장하고 다운로드 URL을 반환
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