package com.luckycharmfairy.data.model

data class Team(
    var sport: String = "",
    var name: String = "",
    var teamcolor: String = "",
    var home: String = "",
    var fans: Int = 0,
    var youtube: String = "",
    var instagram: String = "",
    var facebook: String = "",
    var homepage: String = "",
    var players: MutableList<String> = mutableListOf()
)
