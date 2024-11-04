package com.luckycharmfairy.data.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.luckycharmfairy.data.model.Match
import com.luckycharmfairy.data.model.Post
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.data.model.sampleBitmap
import com.luckycharmfairy.presentation.UiState
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.IOException

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val db = FirebaseFirestore.getInstance()

    private val _uiState: MutableLiveData<UiState<Any>> = MutableLiveData()
    val uiState: LiveData<UiState<Any>> = _uiState

    private val _users = MutableLiveData<MutableList<User>>()
    val users : LiveData<MutableList<User>> get() = _users

    private val _signingInUser = MutableLiveData<User?>()
    val signingInUser : LiveData<User?> get() = _signingInUser

    private val _currentUser = MutableLiveData<User?>()
    val currentUser : LiveData<User?> get() = _currentUser

    private val _currentUserBlockedUsers = MutableLiveData<MutableList<String>?>()
    val currentUserBlockedUsers : LiveData<MutableList<String>?> get() = _currentUserBlockedUsers

    private val _selectedMonthMatchdays = MutableLiveData<MutableList<String>>()
    val selectedMonthMatchdays : LiveData<MutableList<String>> get() = _selectedMonthMatchdays

    private var _selectedDayMatches = MutableLiveData<MutableList<Match>>()
    val selectedDayMatches : LiveData<MutableList<Match>> get() = _selectedDayMatches

    private val _temporaryMatchData = MutableLiveData<Match?>()
    val temporaryMatchData : LiveData<Match?> get() = _temporaryMatchData

    private val _bitmapBeforeSave = MutableLiveData<Bitmap>()
    val bitmapBeforeSave : LiveData<Bitmap> get() = _bitmapBeforeSave
    private val _temporaryImageUrls = MutableLiveData<MutableList<String>>()
    val temporaryImageUrls : LiveData<MutableList<String>> get() = _temporaryImageUrls

    fun addUser(user: User) {
        viewModelScope.launch {
            runCatching {
                db.collection("user")
                    .document(user.email)
                    .set(user)
            }.onFailure {
                Log.e(TAG, "addUser() failed! : ${it.message}")
                handleException(it)
            }
        }
    }

    fun addMyPost(email: String, post: Post) {
        viewModelScope.launch {
            runCatching {
                db.collection("user")
                    .document(email)
                    .update("mypost", FieldValue.arrayUnion(post))
                    .addOnSuccessListener {
                        println("CurrentUser의 MyPost에 ${post.id} 추가 성공")
                    }
                    .addOnFailureListener { exception ->
                        println("CurrentUser의 MyPost에 ${post.id} 추가 실패 / $exception")
                    }
            }.onFailure {
                Log.e(TAG, "addMyPost() failed! : ${it.message}")
                handleException(it)
            }
        }
    }

//    fun editMyPost(email: String, edittedPost: Post) {
//        viewModelScope.launch {
//            runCatching {
//                val userRef = db.collection("user").document(email)
//                db.runTransaction { transaction ->
//                    val userSnapshot = transaction.get(userRef)
//                    val user = userSnapshot.toObject(User::class.java)
//                    val myposts = user?.myposts ?: emptyList()
//
//                    val mypostIndex = myposts.indexOfFirst { it.id == edittedPost.id }
//                    if (mypostIndex != -1) {
//                        val updatedMyPosts = myposts.toMutableList()
//                        updatedMyPosts[mypostIndex] = edittedPost
//                        transaction.update(userRef, "mypost", updatedMyPosts)
//                    }
//                }.addOnSuccessListener {
//                    println("CurrentUser의 MyPost에 ${edittedPost.id} 업데이트 성공")
//                }
//                    .addOnFailureListener { exception ->
//                        println("CurrentUser의 MyPost에 ${edittedPost.id} 업데이트 실패 / $exception")
//                    }
//            }.onFailure {
//                Log.e(TAG, "editMyPost() failed! : ${it.message}")
//                handleException(it)
//            }
//        }
//    }

//    fun deleteMyPost(email: String, post: Post) {
//        viewModelScope.launch {
//            runCatching {
//                val userRef = db.collection("user").document(email)
//                db.runTransaction { transaction ->
//                    val userSnapshot = transaction.get(userRef)
//                    val user = userSnapshot.toObject(User::class.java)
//                    val myposts = user?.myposts ?: emptyList()
//
//                    val mypostIndex = myposts.indexOfFirst { it.id == post.id }
//                    if (mypostIndex != -1) {
//                        val updatedMyPosts = myposts.toMutableList()
//                        updatedMyPosts.removeAt(mypostIndex)
//                        transaction.update(userRef, "mypost", updatedMyPosts)
//                    }
//                }.addOnSuccessListener {
//                    println("CurrentUser의 MyPost에서 ${post.id} 삭제 성공")
//                }
//                    .addOnFailureListener { exception ->
//                        println("CurrentUser의 MyPost에서 ${post.id} 삭제 실패 / $exception")
//                    }
//            }.onFailure {
//                Log.e(TAG, "deleteMyPost() failed! : ${it.message}")
//                handleException(it)
//            }
//        }
//    }

    fun findUser(_email: String) {
        viewModelScope.launch {
            runCatching {
                db.collection("user")
                    .whereEqualTo("email", _email)
                    .get()
                    .addOnSuccessListener { result ->
                        if (result != null) {
                            for (document in result) {
                                val user = document.toObject(User::class.java)
                                _signingInUser.value = user
                            }
                        } else {
                            _signingInUser.value = null
                        }
                    }
                    .addOnFailureListener{
                        _signingInUser.value = null
                    }
            }.onFailure {
                Log.e(TAG, "findUser() failed! : ${it.message}")
                handleException(it)
            }
        }
    }

    fun setCurrentUser(_email: String) {
        viewModelScope.launch {
            db.collection("user")
                .document(_email)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        Log.e(TAG, "setCurrentUser() failed! : ${exception.message}")
                        _currentUser.value = null
                        handleException(exception)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val user = snapshot.toObject(User::class.java)
                        _currentUser.value = user
                    } else {
                        _currentUser.value = null
                    }
                }
        }
    }

    fun getSelectedMonthMatchdays(_email: String, selectedSport: String, selectedYear: String, selectedMonth: String) {
        viewModelScope.launch {
            runCatching {
                val userRef = db.collection("user").document(_email)
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(userRef)
                    val currentUser = snapshot.toObject(User::class.java)
                    val matches = currentUser?.matches ?: emptyList()
                    val selectedMonthMatches = matches.filter {
                        it.sport == selectedSport && it.year == selectedYear && it.month == selectedMonth }
                    val matchdays = mutableListOf<String>()
                    selectedMonthMatches.forEach{
                        if (it.date !in matchdays) {
                            matchdays.add(it.day)
                        }
                    }
                    _selectedMonthMatchdays.value = matchdays
                }.addOnSuccessListener {
                    println("Succeeded to get Selected Month's Matchdays")
                }.addOnFailureListener { exception ->
                    println("Failed to get Selected Month's Matchdays: $exception")
                }
            }.onFailure {
                Log.e(TAG, "getSelectedMonthMatchdays() failed! : ${it.message}")
                handleException(it)
            }
        }
    }

    fun getSelectedDateMatches(_email: String, selectedSport: String, selectedYear: String, selectedMonth: String, selectedDate: String) {
        viewModelScope.launch {
            runCatching {
                val userRef = db.collection("user").document(_email)
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(userRef)
                    val currentUser = snapshot.toObject(User::class.java)
                    val matches = currentUser?.matches ?: emptyList()
                    val selectedDayMatches = matches.filter {
                        it.sport == selectedSport &&it.year == selectedYear && it.month == selectedMonth && it.date == selectedDate
                    }.toMutableList()
                    _selectedDayMatches.value = selectedDayMatches
                }.addOnSuccessListener {
                    println("Succeeded to get Selected Date's Matches")
                }.addOnFailureListener { exception ->
                    println("Failed to get Selected Date's Matches: $exception")
                }
            }.onFailure {
                Log.e(TAG, "getSelectedDateMatches() failed! : ${it.message}")
                handleException(it)
            }
        }
    }

    fun saveTemporaryMatchData(matchData: Match) {
        _temporaryMatchData.value = matchData
    }

    fun saveTemporaryImageUrl(imageUrl: String) {
        viewModelScope.launch {
            runCatching {
                _temporaryImageUrls.value?.add(imageUrl)
            }.onFailure {
                Log.e(TAG, "saveTemporaryImageUrl() failed! : ${it.message}")
                handleException(it)
            }
        }
    }

    fun addNewMatch(content: String) {
        viewModelScope.launch {
            runCatching {
                _temporaryMatchData.value?.photos = _temporaryImageUrls.value!!
                _temporaryMatchData.value?.content = content
                val newMatch = _temporaryMatchData.value
                db.collection("user")
                    .document(currentUser.value!!.email)
                    .update("matches", FieldValue.arrayUnion(newMatch))
                    .addOnSuccessListener {
                        println("${newMatch!!.id} 직관 기록 추가 성공")
                    }
                    .addOnFailureListener { exception ->
                        println("${newMatch!!.id} 직관 기록 추가 실패")
                    }
            }.onFailure {
                Log.e(TAG, "addNewMatch() failed! : ${it.message}")
                handleException(it)
            }
        }
    }

    fun getBlockedUsers(){
        db.collection("user")
            .document(currentUser.value!!.email)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e(TAG, "getBlockedUsers() failed! : ${exception.message}")
                    handleException(exception)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val user = snapshot.toObject(User::class.java)
                    val blockedUsers = user?.blockedUsers
                    _currentUserBlockedUsers.value = blockedUsers
                } else {
                    // 예외처리: 스냅샷이 null일 때 처리
                }
            }

    }

    fun updateCurrentUserInfo() {
        viewModelScope.launch {
            runCatching {
                val updatedUserInfo = currentUser.value
                if (updatedUserInfo != null) {
                    db.collection("user").document(currentUser.value!!.email)
                        .set(updatedUserInfo)
                }
            }.onFailure {
                Log.e(TAG, "updateCurrentUserInfo() failed! : ${it.message}")
                handleException(it)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            runCatching {
                _currentUser.value = User()
            }.onFailure {
                Log.e(TAG, "updateCurrentUserInfo() failed! : ${it.message}")
                handleException(it)
            }
        }
    }

    fun deleteID(user: User) {
        viewModelScope.launch {
            runCatching {
                _currentUser.value = null
                db.collection("user")
                    .document(user.email)
                    .delete()
                    .addOnSuccessListener {
                        println("User ${user.email} deleted successfully")
                    }.addOnFailureListener { exception ->
                        println("Failed to delete User ${user.email} : $exception")
                    }

                db.collection("post")
                    .whereEqualTo("posterEmail", user.email)
                    .get()
                    .addOnSuccessListener { posts ->
                        for (post in posts) {
                            post.reference.delete()
                                .addOnSuccessListener {
                                    println("Deleted file with ID: ${post.id}")
                                }
                                .addOnFailureListener { e ->
                                    println("Error deleting file: $e")
                                }
                        }
                    }
                    .addOnFailureListener{ e ->
                        println("Error getting documents: $e")
                    }
            }.onFailure {
                Log.e(TAG, "deleteID() failed! : ${it.message}")
                handleException(it)
            }
        }
    }

    fun addBlockedUser(email: String){
        viewModelScope.launch {
            runCatching {
                var newBlockedUsers = currentUser.value!!.blockedUsers
                newBlockedUsers.add(email)
                currentUser.value!!.blockedUsers = newBlockedUsers
                updateCurrentUserInfo()
            }.onFailure {
                Log.e(TAG, "deleteID() failed! : ${it.message}")
                handleException(it)
            }
        }
    }

    // 갤러리로부터 이미지 값을 받아와서 Bitmap 으로 변환
    suspend fun handleImage(uri: Uri) {
        _bitmapBeforeSave.value = sampleBitmap
        val imageLoader = ImageLoader(getApplication())
        val request = ImageRequest.Builder(getApplication())
            .data(uri)
            .allowHardware(false) // Bitmap을 요청할 때는 false로 설정
            .build()

        val result = imageLoader.execute(request)
        if (result is SuccessResult) {
            _bitmapBeforeSave.value = result.drawable.toBitmap()
        } else {
            // 실패 처리
        }
    }

    // 임시 저장하는 Bitmap 파일을 String 타입의 다운로드 URL 값으로 변환
    fun uploadImageToFirebaseStorage(onSuccess: () -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/${System.currentTimeMillis()}.png")
        val baos = ByteArrayOutputStream()
        bitmapBeforeSave.value?.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                saveUserPhotoUrl(uri.toString())
                _currentUser.value?.photo = uri.toString()
                onSuccess()
            }
        }.addOnFailureListener {
            // 업로드 실패 처리
        }
    }

    fun saveUserPhotoUrl(photoUrl: String) {
        db.collection("user").document(currentUser.value!!.email)
            .update("photo", photoUrl)
    }

    fun getDownloadUrl(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("user").document(currentUser.value!!.email)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val downloadUrl = document.getString("photo")
                    if (downloadUrl != null) {
                        onSuccess(downloadUrl)
                    } else {
                        onFailure(Exception("Download URL not found"))
                    }
                } else {
                    onFailure(Exception("No such document"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    private fun handleException(e: Throwable) {
        when (e) {
            is HttpException -> {
                val errorJsonString = e.response()?.errorBody()?.string()
                Log.e(TAG, "HTTP error: $errorJsonString")
            }

            is IOException -> Log.e(TAG, "Network error: $e")
            else -> Log.e(TAG, "Unexpected error: $e")
        }
    }
}