package com.example.concerttrack.models


import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.DocumentReference
import java.io.Serializable

data class Event(
    var header: String,
    var startDateTime : Timestamp,
    var shortDescription: String,
    var ticketsLink: String = "",
    //location
        var placeName: String,
        var placeAddress: String,
        var placeLatLng: GeoPoint,
    val artist: DocumentReference
) : Serializable