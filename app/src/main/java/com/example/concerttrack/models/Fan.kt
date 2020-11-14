package com.example.concerttrack.models

import java.io.Serializable

data class Fan(
    val id: String,
    val email: String,
    val name: String,
    var interestedEvents: MutableList<String> = mutableListOf(),
    var favouritesArtists: MutableList<String> = mutableListOf(),
    var myEvents: MutableList<String> = mutableListOf()
) : Serializable