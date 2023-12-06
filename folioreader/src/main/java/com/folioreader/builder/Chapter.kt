package com.folioreader.builder

data class Chapter(
    val sourceUrl: String,
    val title: String,
    var newArc: String,
    var position: Int
) {
    constructor(baseUrl: String, title: String) : this(baseUrl, title, "", -1)

}