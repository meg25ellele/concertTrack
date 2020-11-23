package com.example.concerttrack.models
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

data class Event(
    var header: String,
    var startDateTime : String,
    var shortDescription: String,
    var ticketsLink: String = "",
    //location
        var placeName: String,
        var placeAddress: String,
        var placeLat: Double,
        var placeLng: Double,
    val artistReferencePath: String,
    val id: String? = null
) : Serializable {

    fun getEventString(): String = header.toLowerCase() + startDateTime.toLowerCase() +
            shortDescription.toLowerCase() + ticketsLink.toLowerCase() + placeName.toLowerCase() +
            placeAddress.toLowerCase()

}