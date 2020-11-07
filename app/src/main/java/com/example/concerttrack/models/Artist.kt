package com.example.concerttrack.models

import android.net.Uri
import java.io.FileDescriptor

data class Artist(
    val id:String,
    val email: String,
    val name: String,
    var description:String = "",
    var facebookLink:String = "",
    var youtubeLink:String = "",
    var spotifyLink: String = "",
    var myGenres: List<String>? = null
)