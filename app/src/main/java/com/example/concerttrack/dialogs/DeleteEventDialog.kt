package com.example.concerttrack.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDialogFragment
import java.lang.ClassCastException

class DeleteEventDialog(val listener: DeleteEventDialogListener): AppCompatDialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
       val builder   = AlertDialog.Builder(activity)
        builder.setTitle("Uwaga!")
            .setMessage("Czy na pewno chcesz usunąć to wydarzenie?")
            .setPositiveButton("TAK",DialogInterface.OnClickListener { dialog, which ->
                listener.onYesClicked()
            })
            .setNegativeButton("NIE",DialogInterface.OnClickListener { dialog, which ->  })

        return builder.create()
    }

    interface DeleteEventDialogListener {
        fun onYesClicked()
    }

}