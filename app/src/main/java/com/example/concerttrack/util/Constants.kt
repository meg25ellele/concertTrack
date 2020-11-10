package com.example.concerttrack.util

import android.widget.EditText
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Constants {
    companion object {
        const val USER_TYPE = "com.example.concerttrack.util.constants.user_type"

        const val ARTIST_TYPE_STR = "artist"
        const val FAN_TYPE_STR = "fan"
        const val GUEST_TYPE_STR = "guest"

        const val DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm"
        const val DATE_FORMAT = "dd-MM-yyyy"
        const val TIME_FORMAT = "HH:mm"

        const val FIREBASE_EXCEPTION_TAG = "firebaseError"


        const val GALLERY_REQUEST_CODE = 123
        const  val PICK_IMAGE_REQUEST = 1

        const val ERROR_DIALOG_REQUEST = 9001

        val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).withZone(ZoneId.systemDefault())

        val FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION
        val COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION
        val LOCATION_PERMISSION_REQUEST_CODE = 1234
        val DEFAULT_ZOOM = 15f
    }

}

fun EditText.content() = text.toString()