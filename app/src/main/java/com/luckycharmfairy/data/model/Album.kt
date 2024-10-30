package com.luckycharmfairy.data.model

data class Album(
    var id: String = "",
    var uploaderEmail: String = "",
    var category: String = "",
    var title: String = "",
    var mainImage: String = "",
    var images: MutableList<String> = mutableListOf(),
    var scrap: Int = 0
)
