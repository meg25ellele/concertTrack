package com.example.concerttrack.util

import android.app.Application
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.dmoral.toasty.Toasty


fun Application.showToastSuccess(stringId: Int) =
    Toasty.success(this, resources.getText(stringId), Toast.LENGTH_LONG, true).show()

fun Application.showToastInfo(stringId: Int) =
    Toasty.info(this, resources.getText(stringId), Toast.LENGTH_LONG, true).show()

fun Application.showToastError(stringId: Int) =
    Toasty.error(this, resources.getText(stringId), Toast.LENGTH_LONG, true).show()

 fun Application.showToastWarning(stringId: Int) =
     Toasty.warning(this, resources.getText(stringId), Toast.LENGTH_LONG, true).show()


fun AppCompatActivity.showToastSuccess(stringId: Int) =
    Toasty.success(this, resources.getText(stringId), Toast.LENGTH_LONG, true).show()

fun AppCompatActivity.showToastInfo(stringId: Int) =
    Toasty.info(this, resources.getText(stringId), Toast.LENGTH_LONG, true).show()

fun AppCompatActivity.showToastError(stringId: Int) =
    Toasty.error(this, resources.getText(stringId), Toast.LENGTH_LONG, true).show()

fun AppCompatActivity.showToastWarning(stringId: Int) =
    Toasty.warning(this, resources.getText(stringId), Toast.LENGTH_LONG, true).show()
