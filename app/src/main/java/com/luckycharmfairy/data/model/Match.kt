package com.luckycharmfairy.data.model

data class Match (
    var id: String = "",
    var writerEmail: String = "",
    var year: String = "",
    var month: String = "",
    var date: String = "",
    var day: String = "",
    var time: String = "",
    var location: String = "",
    var weather: String = "",
    var feeling: String = "",
    var sport: String = "",
    var home: Team = Team(),
    var away: Team = Team(),
    var homescore: Int = 0,
    var awayscore: Int = 0,
    var result: String = "", // WIN, LOSE, TIE, CANCEL, NONE
    var myteam: Team = Team(),
    var mvp: String = "",
    var photos: MutableList<String> = mutableListOf(),
    var content: String = ""
)

public val baseballLocations = listOf(
    "대전베이스볼드림파크",
    "고척스카이돔",
    "잠실종합운동장",
    "부산사직야구장",
    "대구삼성라이온즈파크",
    "광주기아챔피언스필드",
    "인천SSG랜더스필드",
    "수원케이티위즈파크",
    "창원NC파크",
    "직접 입력"
    )

public val menFootballLocations = listOf(    // K리그1
    "강릉종합운동장",
    "춘천송암스포츠타운",
    "광주축구전용구장",
    "광주월드컵경기장",
    "김천종합스포츠타운",
    "DGB대구은행파크",
    "대전월드컵경기장",
    "서울월드컵경기장",
    "수원종합운동장",
    "울산문수축구경기장",
    "울산종합운동장",
    "인천축구전용경기장",
    "전주월드컵경기장",
    "제주월드컵경기장",
    "포항스틸야드",
    "직접 입력"
)

public val menBasketballLocations = listOf(
    "사직실내체육관",
    "수원KT소닉붐아레나",
    "원주종합체육관",
    "창원실내체육관",
    "잠실학생체육관",
    "울산동천체육관",
    "대구실내체육관",
    "고양소노아레나",
    "안양정관장아레나",
    "잠실실내체육관",
    "직접 입력"
)

public val menVolleyballLocations = listOf(
    "계양체육관",
    "상록수체육관",
    "장충체육관",
    "유관순체육관",
    "수원실내체육관",
    "충무체육관",
    "의정부실내체육관",
    "직접 입력"
)

public val womenVolleyballLocations = listOf(
    "수원실내체육관",
    "인천삼산월드체육관",
    "충무체육관",
    "장충체육관",
    "화성실내체육관",
    "김천실내체육관",
    "염주종합체육관",
    "직접 입력"
)