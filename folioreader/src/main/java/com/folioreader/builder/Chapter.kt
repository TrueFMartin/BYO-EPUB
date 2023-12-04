package com.folioreader.builder

data class Chapter(
    val sourceUrl: String,
    val title: String,
    var newArc: String
) {
    constructor(baseUrl: String, title: String) : this(baseUrl, title, "") {
    }
}