package com.example.documentmedia

data class Model(
    val id: Int,
    val date: String,
    var name: String,
    val duration: String,
    var artist: String,
    var coverArtUrl: String,
    var url: String,
)
