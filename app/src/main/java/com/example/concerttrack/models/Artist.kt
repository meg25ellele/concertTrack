package com.example.concerttrack.models

import android.net.Uri
import java.io.FileDescriptor

data class Artist(
    val id:String,
    val email: String,
    val name: String,
    var description:String? = null,
    var facebookLink:String? = null,
    var youtubeLink:String? = null,
    var spotifyLink: String? = null,
    var myGenres: List<String>? = null
)