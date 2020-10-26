package com.example.concerttrack.util

import android.app.Application
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import es.dmoral.toasty.Toasty

//for Application
fun Application.showToastSuccess(stringId: Int) =
    Toasty.success(this, resources.getText(stringId), Toast.LENGTH_LONG, true).show()

fun Application.showToastInfo(stringId: Int) =
    Toasty.info(this, resources.getText(stringId), Toast.LENGTH_LONG, true).show()

fun Application.showToastError(stringId: Int) =
    Toasty.error(this, resources.getText(stringId), Toast.LENGTH_LONG, true).show()

 fun Application.showToastWarning(stringId: Int) =
     Toasty.warning(this, resources.getText(stringId), Toast.LENGTH_LONG, true).show()

//for AppCompatActivity
fun AppCompatActivity.showToastSuccess(stringId: Int) =
    Toasty.success(this, resources.getText(stringId), Toast.LENGTH_LONG, true).show()

fun AppCompatActivity.showToastInfo(stringId: Int) =
    Toasty.info(this, resources.getText(stringId), Toast.LENGTH_LONG, true).show()

fun AppCompatActivity.showToastError(stringId: Int) =
    Toasty.error(this, resources.getText(stringId), Toast.LENGTH_LONG, true).show()

fun AppCompatActivity.showToastWarning(stringId: Int) =
    Toasty.warning(this, resources.getText(stringId), Toast.LENGTH_LONG, true).show()

//for Fragment
fun Fragment.showToastSuccess(stringId: Int) =
    Toasty.success(activity!!.applicationContext, resources.getText(stringId), Toast.LENGTH_LONG, true).show()

fun Fragment.showToastInfo(stringId: Int) =
    Toasty.info(activity!!.applicationContext, resources.getText(stringId), Toast.LENGTH_LONG, true).show()

fun Fragment.showToastError(stringId: Int) =
    Toasty.error(activity!!.applicationContext, resources.getText(stringId), Toast.LENGTH_LONG, true).show()

fun Fragment.showToastWarning(stringId: Int) =
    Toasty.warning(activity!!.applicationContext, resources.getText(stringId), Toast.LENGTH_LONG, true).show()