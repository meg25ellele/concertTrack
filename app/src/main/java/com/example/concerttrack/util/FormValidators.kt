package com.example.concerttrack.util

import android.util.Patterns
import java.time.ZonedDateTime

object FormValidators {


    fun isDateTimeOK(date: String, time: String): Boolean {
        val dateAndTime = "$date $time"
        val parsedDate = ZonedDateTime.parse(dateAndTime, Constants.DATE_TIME_FORMATTER)

        return parsedDate.isAfter(ZonedDateTime.now())
    }

    fun isHeaderTooLong(header: String): Boolean {
        return header.length > 40
    }

    fun isPlaceNameTooLong(placeName:String):Boolean {
        return placeName.length > 40
    }

    fun isInputEmpty(input: String): Boolean {
        if(input.trim()=="") return true
        return false
    }

    fun isFanNameTooLong(name: String): Boolean {
        return name.length > 15
    }

    fun arePasswordsEquals(password: String, repeatPassword: String): Boolean {
        return  password==repeatPassword
    }

    fun isPasswordTooShort(password: String): Boolean {
        return password.length <6
    }

    fun isNameChanged(newName: String, previousName: String): Boolean {
        return newName!=previousName
    }

    //Patterns not in unit tests
    fun isEmailCorrect(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}