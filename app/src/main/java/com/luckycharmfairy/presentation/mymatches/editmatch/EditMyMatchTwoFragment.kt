package com.luckycharmfairy.presentation.mymatches.editmatch

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.storage.FirebaseStorage
import com.luckycharmfairy.presentation.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentEditMyMatchTwoBinding
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentMyMatchesBinding
import com.luckycharmfairy.presentation.mymatches.MyMatchesFragment
import com.luckycharmfairy.presentation.mymatches.addmatches.AddMyMatchOneFragment
import com.luckycharmfairy.presentation.mymatches.addmatches.PhotoAdapter
import com.luckycharmfairy.presentation.mymatches.matchdetail.MatchDetailFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

private const val ARG_PARAM1 = "param1"
class EditMyMatchTwoFragment : Fragment() {
    private var param1: String? = null

    lateinit var binding : FragmentEditMyMatchTwoBinding

    private var matchContent: String = ""

    private var matchId: String = ""

    private val photoAdapter by lazy { PhotoAdapter() }

    private val userViewModel: UserViewModel by activityViewModels {
        viewModelFactory { initializer { UserViewModel(requireActivity().application) } }
    }

    private var imageResources = mutableListOf<String>()

    private lateinit var pickImageLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }
    companion object {
        fun newInstance(param1: String) =
            EditMyMatchTwoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditMyMatchTwoBinding.inflate(inflater, container, false)

        binding.recyclerviewPhoto.adapter = photoAdapter
        binding.recyclerviewPhoto.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        matchId = param1.toString()

        userViewModel.temporaryMatchData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                matchContent = data.content
            }
            binding.etContent.setText(matchContent)
            if (data != null) {
                imageResources = data.photos
            }
            photoAdapter.submitList(imageResources)
            userViewModel.saveTemporaryImageUrl(imageResources)
        }

        val matchDetail = MatchDetailFragment.newInstance(matchId)
        val editMyMatchOneFragment = requireActivity().supportFragmentManager.findFragmentByTag("EditMyMatchOneFragment")
        binding.btnClose.setOnClickListener{
            userViewModel.initializeTemporaryImageUrls()
            requireActivity().supportFragmentManager.beginTransaction().apply {
                remove(this@EditMyMatchTwoFragment)
                if (editMyMatchOneFragment != null) {
                    remove(editMyMatchOneFragment)
                }
                add(R.id.main_frame, matchDetail)
                addToBackStack(null)
                commit()
            }
        }

        // 갤러리에서 이미지 선택
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val uris= mutableListOf<Uri>()
                    val maxSelection = 3

                    if (data!!.clipData!!.itemCount + imageResources.size <= maxSelection) {
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
                    } else {
                        Toast.makeText(requireContext(), "사진은 최대 3개까지 선택 가능합니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        binding.btnAddNewPhoto.setOnClickListener{
            Toast.makeText(this.requireContext(), "올리실 사진을 선택해주세요.", Toast.LENGTH_SHORT).show()
            pickImages()
        }

        photoAdapter.itemClick = object : PhotoAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                imageResources.removeAt(position)
                photoAdapter.updateData(imageResources)
            }
        }

        binding.btnSave.setOnClickListener {
            matchContent = binding.etContent.text.toString()
            userViewModel.editMatch(matchContent)
            Toast.makeText(requireContext(), "직관 기록이 수정되었습니다.", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.beginTransaction().apply {
                remove(this@EditMyMatchTwoFragment)
                if (editMyMatchOneFragment != null) {
                    remove(editMyMatchOneFragment)
                }
                add(R.id.main, matchDetail)
                addToBackStack(null)
                commit()
            }
        }

        binding.btnBack.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    R.anim.slide_in_left,
//                    R.anim.slide_in_right,
//                    R.anim.slide_out_left,
                    R.anim.slide_out_right
                )
                hide(this@EditMyMatchTwoFragment)
                if (editMyMatchOneFragment != null) {
                    show(editMyMatchOneFragment)
                } else {
                    add(R.id.main_frame, EditMyMatchOneFragment(), "EditMyMatchOneFragment")
                }
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
//                        userViewModel.saveTemporaryImageUrl(url)
                        // 모든 이미지가 처리된 후에 ViewPager를 업데이트
                        if (uris.indexOf(uri) == uris.size - 1) {
                            photoAdapter.updateData(imageResources)
                        }
                    }
                    userViewModel.saveTemporaryImageUrl(imageResources)
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