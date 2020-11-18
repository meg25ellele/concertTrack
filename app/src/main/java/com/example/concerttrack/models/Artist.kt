package com.example.concerttrack.models


import java.io.Serializable

data class Artist(
    val id:String,
    val email: String? =null,
    val name: String,
    var description:String = "",
    var facebookLink:String = "",
    var youtubeLink:String = "",
    var spotifyLink: String = "",
    var myGenres: List<String>? = null
) : Serializable