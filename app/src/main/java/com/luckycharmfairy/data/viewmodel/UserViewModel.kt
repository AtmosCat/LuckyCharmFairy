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

    private val _winCount = MutableLiveData<Int?>()
    val winCount : LiveData<Int?> get() = _winCount

    private val _loseCount = MutableLiveData<Int?>()
    val loseCount : LiveData<Int?> get() = _loseCount

    private val _tieCount = MutableLiveData<Int?>()
    val tieCount : LiveData<Int?> get() = _tieCount

    private val _cancelCount = MutableLiveData<Int?>()
    val cancelCount : LiveData<Int?> get() = _cancelCount

    private val _noResultCount = MutableLiveData<Int?>()
    val noResultCount : LiveData<Int?> get() = _noResultCount

    private val _matchResultCount = MutableLiveData<MutableList<Int>>()
    val matchResultCount : LiveData<MutableList<Int>> get() = _matchResultCount

    private val _homeMatchResultCount = MutableLiveData<MutableList<Int>>()
    val homeMatchResultCount : LiveData<MutableList<Int>> get() = _homeMatchResultCount

    private val _awayMatchResultCount = MutableLiveData<MutableList<Int>>()
    val awayMatchResultCount : LiveData<MutableList<Int>> get() = _awayMatchResultCount

    private val _bitmapBeforeSave = MutableLiveData<Bitmap>()
    val bitmapBeforeSave : LiveData<Bitmap> get() = _bitmapBeforeSave

    private val _temporaryImageUrls = MutableLiveData<MutableList<String>>()
    val temporaryImageUrls : LiveData<MutableList<String>> get() = _temporaryImageUrls

    init {
        _currentUser.value = User()
    }

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
                    var selectedMonthMatches = mutableListOf<Match>()
                    if (selectedSport != "전체 종목") {
                        selectedMonthMatches = matches.filter {
                            it.sport == selectedSport && it.year == selectedYear && it.month == selectedMonth }.toMutableList()
                    } else {
                        selectedMonthMatches = matches.filter {
                            it.year == selectedYear && it.month == selectedMonth }.toMutableList()
                    }
                    val matchdays = mutableListOf<String>()
                    selectedMonthMatches.forEach{
                        if (it.date !in matchdays) {
                            matchdays.add(it.date)
                        }
                    }
                    _selectedMonthMatchdays.postValue(matchdays)
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
                    var selectedDayMatches = mutableListOf<Match>()
                    if (selectedSport != "전체 종목") {
                        selectedDayMatches = matches.filter {
                            it.sport == selectedSport && it.year == selectedYear && it.month == selectedMonth && it.date == selectedDate
                        }.toMutableList()
                    } else {
                        selectedDayMatches = matches.filter {
                            it.year == selectedYear && it.month == selectedMonth && it.date == selectedDate
                        }.toMutableList()
                    }
                    _selectedDayMatches.postValue(selectedDayMatches)
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

    fun saveTemporaryImageUrl(imageUrls: MutableList<String>) {
        viewModelScope.launch {
            runCatching {
                _temporaryImageUrls.value = imageUrls
            }.onFailure {
                Log.e(TAG, "saveTemporaryImageUrl() failed! : ${it.message}")
                handleException(it)
            }
        }
    }

    fun initializeTemporaryImageUrls() {
        viewModelScope.launch {
            runCatching {
                _temporaryImageUrls.value = mutableListOf()
            }.onFailure {
                Log.e(TAG, "initializeTemporaryImageUrls() failed! : ${it.message}")
                handleException(it)
            }
        }
    }

    fun addNewMatch(content: String) {
        viewModelScope.launch {
            runCatching {
                if (_temporaryImageUrls.value.isNullOrEmpty()) {
                    _temporaryMatchData.value?.photos = mutableListOf<String>()
                } else {
                    _temporaryMatchData.value?.photos = _temporaryImageUrls.value!!
                }
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

    fun editMatch(matchContent: String) {
        viewModelScope.launch {
            runCatching {
                if (_temporaryImageUrls.value.isNullOrEmpty()) {
                    _temporaryMatchData.value?.photos = mutableListOf()
                } else {
                    _temporaryMatchData.value?.photos = _temporaryImageUrls.value!!
                }
                _temporaryMatchData.value?.content = matchContent
                val edittedMatch = _temporaryMatchData.value
                val matchToEdit = currentUser.value!!.matches.find { it.id == edittedMatch!!.id }
                val index = currentUser.value!!.matches.indexOf(matchToEdit)
                if (edittedMatch != null) {
                    currentUser.value!!.matches[index] = edittedMatch
                }
                db.collection("user")
                    .document(currentUser.value!!.email)
                    .set(currentUser.value!!)
            }.onFailure {
                Log.e(TAG, "editMatch() failed! : ${it.message}")
                handleException(it)
            }
        }
    }

    fun deleteMatch(_id: String) {
        viewModelScope.launch {
            runCatching {
                val matchToDelete = currentUser.value!!.matches.find { it.id == _id }
                val index = currentUser.value!!.matches.indexOf(matchToDelete)
                currentUser.value!!.matches.removeAt(index)
                db.collection("user")
                    .document(currentUser.value!!.email)
                    .set(currentUser.value!!)
            }.onFailure {
                Log.e(TAG, "deleteMatch() failed! : ${it.message}")
                handleException(it)
            }
        }
    }

    fun getMatchResultStat() {
        viewModelScope.launch {
            runCatching {
                val matches = currentUser.value!!.matches
                var win = 0
                var lose = 0
                var tie = 0
                var cancel = 0
                var noResult = 0
                matches.forEach {
                    when (it.result) {
                        "승리" -> win += 1
                        "패배" -> lose += 1
                        "무승부" -> tie += 1
                        "경기 취소" -> cancel += 1
                        "타팀 직관" -> noResult += 1
                    }
                }
                _matchResultCount.postValue(mutableListOf(win, lose, tie, cancel, noResult))
            }.onFailure {
                Log.e(TAG, "getMatchResultStat() failed! : ${it.message}")
                handleException(it)
            }
        }
    }

    fun getHomeAwayMatchStat() {
        viewModelScope.launch {
            runCatching {
                val homeMatches = currentUser.value!!.matches.filter { it.myteam == "홈 팀" }
                val awayMatches = currentUser.value!!.matches.filter { it.myteam == "어웨이 팀" }
                var homeWin = 0
                var homeLose = 0
                var homeTie = 0
                var awayWin = 0
                var awayLose = 0
                var awayTie = 0
                homeMatches.forEach {
                    when (it.result) {
                        "승리" -> homeWin += 1
                        "패배" -> homeLose += 1
                        "무승부" -> homeTie += 1
                    }
                }
                awayMatches.forEach {
                    when (it.result) {
                        "승리" -> awayWin += 1
                        "패배" -> awayLose += 1
                        "무승부" -> awayTie += 1
                    }
                }
                _homeMatchResultCount.postValue(mutableListOf(homeWin, homeLose, homeTie))
                _awayMatchResultCount.postValue(mutableListOf(awayWin, awayLose, awayTie))
            }.onFailure {
                Log.e(TAG, "getHomeAwayMatchStat() failed! : ${it.message}")
                handleException(it)
            }
        }
    }

    fun editMySport(mySports: MutableList<String>) {
        viewModelScope.launch {
            runCatching {
                currentUser.value!!.mysports = mySports
                db.collection("user")
                    .document(currentUser.value!!.email)
                    .set(currentUser.value!!)
            }.onFailure {
                Log.e(TAG, "editMySport() failed! : ${it.message}")
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
    fun updateWholeCurrentUserInfo(updatedUserInfo: User) {
        viewModelScope.launch {
            runCatching {
                db.collection("user").document(updatedUserInfo.email)
                    .set(updatedUserInfo)
            }.onFailure {
                Log.e(TAG, "updateWholeCurrentUserInfo() failed! : ${it.message}")
                handleException(it)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            runCatching {
                _currentUser.value = User()
            }.onFailure {
                Log.e(TAG, "signOut() failed! : ${it.message}")
                handleException(it)
            }
        }
    }

    fun deleteID(user: User) {
        viewModelScope.launch {
            runCatching {
                _currentUser.value = User()
                db.collection("user")
                    .document(user.email)
                    .delete()
                    .addOnSuccessListener {
                        println("User ${user.email} deleted successfully")
                    }.addOnFailureListener { exception ->
                        println("Failed to delete User ${user.email} : $exception")
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