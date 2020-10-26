package com.example.concerttrack.util

import android.widget.EditText

class Constants {
    companion object {
        const val USER_TYPE = "com.example.concerttrack.util.constants.user_type"

        const val ARTIST_TYPE_STR = "artist"
        const val FAN_TYPE_STR = "fan"


        const val GALLERY_REQUEST_CODE = 123
        const  val PICK_IMAGE_REQUEST = 1
    }

}

fun EditText.content() = text.toString()