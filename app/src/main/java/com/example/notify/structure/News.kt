package com.example.notify.structure

data class News (
    val id_news: Int,
    val domain: String?,
    val title: String?,
    val subtitle: String?,
    val link: String?,
    val image: String?,
    val language: String?,
    val author: String?,
    val type: Int?
)