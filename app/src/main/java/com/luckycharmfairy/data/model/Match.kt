package com.luckycharmfairy.data.model

data class Match (
    var id: String = "",
    var writerEmail: String = "",
    var year: String = "",
    var month: String = "",
    var date: String = "",
    var time: String = "",
    var location: String = "",
    var weather: String = "",
    var feeling: String = "",
    var sport: String = "",
    var home: String = "",
    var away: String = "",
    var homescore: Int = 0,
    var awayscore: Int = 0,
    var result: String = "", // WIN, LOSE, TIE, CANCEL, NONE
    var myteam: String = "",
    var mvp: String = "",
    var photos: MutableList<String> = mutableListOf(),
    var content: String = ""
)