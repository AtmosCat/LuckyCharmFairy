package com.luckycharmfairy.data.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import java.util.UUID

data class User(val email: String = "",
                var pw: String = "",
                var nickname: String = "익명의 팬"+UUID.randomUUID().toString(),
                var photo: String = "", // 프로필사진
                var membership: String = "", // 멤버십
                var mysports: MutableList<String> = mutableListOf(), // 나의 종목
                var myteams : MutableList<String> = mutableListOf(), // 마이팀 - Team
                var myplayers : MutableList<String> = mutableListOf(), // 마이선수 - Player
                var matches : MutableList<Match> = mutableListOf(), // 직관 경기 기록 - Match
                var followers: MutableList<String> = mutableListOf(), // 팔로워계정 - User
                var followings: MutableList<String> = mutableListOf(), // 팔로잉계정 - User
                var myposts: MutableList<String> = mutableListOf(), // 내가올린글(찐팬구역) - Post
                var likedposts: MutableList<String> = mutableListOf(), // 내가 저장한 글(찐팬구역) - Post
                var myalbums: MutableList<String> = mutableListOf(), // 내 앨범 - Album
                var savedalbums: MutableList<String> = mutableListOf(), // 저장한 앨범 - Album
                var blockedUsers: MutableList<String> = mutableListOf(), // 차단한 유저 - User
                var badges: MutableList<String> = mutableListOf(), // 획득한 배지 - Badge 아니면 그냥 String?
)

fun createSampleBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()

    paint.color = Color.WHITE
    canvas.drawCircle(500f, 450f, 150f, paint)

    paint.color = Color.WHITE
    val path = android.graphics.Path()
    path.addCircle(500f, 1000f, 350f, Path.Direction.CCW)
    canvas.drawPath(path, paint)

    return bitmap
}

public val sampleBitmap = createSampleBitmap()