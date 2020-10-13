package com.example.concerttrack.util

import android.widget.EditText

class Constants {
    companion object {
        const val USER_TYPE = "com.example.concerttrack.util.constants.user_type"

        const val ARTIST_TYPE_STR = "artist"
        const val FAN_TYPE_STR = "fan"

        const val RC_SIGN_IN = 123
    }
}

fun EditText.content() = text.toString()