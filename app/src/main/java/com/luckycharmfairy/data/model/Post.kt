package com.luckycharmfairy.data.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

data class Post(
    val id: String = "",
    val category: String = "",
    val posterEmail: String = "",
    val posterPhoto: String = "",
    val posterNickname: String = "",
    val title: String = "",
    val detail: String = "",
    val detailPhoto: MutableList<String> = mutableListOf(),
    val comments: MutableList<String> = mutableListOf(),
    var like: Int = 0,
    val report: Boolean = false,
    val timestamp: String? = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
)

data class Comment(
    val id: String = "",
    val commenterEmail: String = "",
    val commenterPhoto: String = "",
    val commenterNickname: String = "",
    val detail: String = "",
    val like: Int = 0,
    var reply: MutableList<String> = mutableListOf(),
    val report: Boolean = false,
    val timestamp: String? = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
)

data class Reply(
    val id: String = "",
    val replierEmail: String = "",
    val replierPhoto: String = "",
    val replierNickname: String = "",
    val detail: String = "",
    val like: Int = 0,
    val report: Boolean = false,
    val timestamp: String? = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
)
