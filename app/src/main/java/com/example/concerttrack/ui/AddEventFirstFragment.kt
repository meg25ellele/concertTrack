package com.example.concerttrack.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.concerttrack.R
import com.example.concerttrack.util.Constants
import com.example.concerttrack.util.Constants.Companion.DATE_FORMAT
import com.example.concerttrack.util.Constants.Companion.TIME_FORMAT
import com.example.concerttrack.util.FormValidators
import com.example.concerttrack.util.content
import com.example.concerttrack.util.showToastError
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.android.synthetic.main.add_event_first_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class AddEventFirstFragment:Fragment(R.layout.add_event_first_fragment){


    private var mContext: Context? = null



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exitBtn.setOnClickListener {
            startActivity(Intent(activity,ArtistMainPageActivity::class.java))
            activity?.finish()
        }

        dateBtn.setOnClickListener {
            showCalendarAndPickDate()
        }

        timeBtn.setOnClickListener{
            showClockAndPickTime()
        }

        nextBtn.setOnClickListener {
            if(isFormCorrect()) {
                val bundle = bundleOf(getString(R.string.header) to eventHeaderET.text.toString(),
                getString(R.string.startDate) to dateET.text.toString(), getString(R.string.startTime) to timeET.text.toString(),
                getString(R.string.description) to eventDescET.text.toString(),
                getString(R.string.ticketsLink) to ticketsLink.text.toString())

                if(isServicesOK()) {
                    view.findNavController().navigate(R.id.action_addEventFirstFragment_to_addEventSecondFragment,bundle)
                }

            }
        }

        eventHeaderET.addTextChangedListener {
            text_input_event_header.error = null
        }

        eventDescET.addTextChangedListener {
            emptyDescMsg.visibility = View.INVISIBLE
            eventDescInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.secondary_text_material_light))
            eventDescET.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background)
        }

        dateET.addTextChangedListener {
            dataInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.secondary_text_material_light))
            dateET.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background)
        }

        timeET.addTextChangedListener {
            timeInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.secondary_text_material_light))
            timeET.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun isServicesOK():Boolean {
        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext)
        when {
            available == ConnectionResult.SUCCESS -> {
                return true
            }
            GoogleApiAvailability.getInstance().isUserResolvableError(available) -> {
                val dialog = GoogleApiAvailability.getInstance().getErrorDialog(activity,available,
                    Constants.ERROR_DIALOG_REQUEST
                )
                dialog.show()
            }
            else -> {
                Toast.makeText(activity,"can't connect", Toast.LENGTH_LONG).show()
            }
        }
        return false
    }

    private fun showCalendarAndPickDate() {
        val cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, day)
            val date = cal.time
            val formatter  = SimpleDateFormat(DATE_FORMAT)
            dateET.setText(formatter.format(date))
        }

        DatePickerDialog(
            mContext!!,
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showClockAndPickTime() {
        val cal = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            val time = cal.time
            val formater  = SimpleDateFormat(TIME_FORMAT)
            timeET.setText(formater.format(time))
        }

        TimePickerDialog(
            mContext!!,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }


    //check header, time, date, and description
    private fun isFormCorrect(): Boolean {
        val isHeaderOk = validateHeader()
        val isTimeOk = validateTime()
        val idDateOk = validateDate()
        val isDescOk = validateDesc()

        return if (isHeaderOk && isTimeOk && idDateOk && isDescOk) {
            isDateAndTimeOK()
        } else {
            false
        }
    }

    private fun isInputNotFilled(input: String, defaultInput: String) : Boolean {
        if(input.trim()==defaultInput) return true
        return false
    }

    private fun validateHeader(): Boolean {
        val headerEditText = text_input_event_header.editText?.content().toString()

        return when {
            FormValidators.isInputEmpty(headerEditText) -> {
                text_input_event_header.error = getString(R.string.notAllowedEmptyField)
                false
            }
            FormValidators.isHeaderTooLong(headerEditText)-> {
                Log.i("header",headerEditText)
                text_input_event_header.error = getString(R.string.tooLongHeader)
                false
            }
            else -> {
                text_input_event_header.error = null
                true
            }
        }
    }

    private fun validateDate(): Boolean {
        val datePlainText = dateET.text.toString()

        return when {
            isInputNotFilled(datePlainText,"dd-MM-yyyy") -> {
                dataInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.red))
                dateET.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background_red)
                false
            }
            else ->  {
                dataInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.secondary_text_material_light))
                dateET.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background)
                true
            }
        }
    }


    private fun validateTime(): Boolean {
        val timePlainText = timeET.text.toString()

        return when {
            isInputNotFilled(timePlainText,"HH:mm") -> {
                timeInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.red))
                timeET.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background_red)
                false
            }
            else ->  {
                timeInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.secondary_text_material_light))
                timeET.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background)
                true
            }
        }
    }
    private fun validateDesc():Boolean {
        val descEditText = eventDescET.text.toString()

        return when {
            isInputNotFilled(descEditText,"") -> {
                eventDescInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.red))
                emptyDescMsg.visibility = View.VISIBLE
                eventDescET.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background_red)
                false
            }
            else -> {
                emptyDescMsg.visibility = View.INVISIBLE
                eventDescInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.secondary_text_material_light))
                eventDescET.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background)
                true
            }
        }
    }

    private fun isDateAndTimeOK():Boolean {

        return if(!FormValidators.isDateTimeOK(dateET.text.toString(),timeET.text.toString())){
            dataInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.red))
            timeInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.red))
            timeET.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background_red)
            dateET.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background_red)
            this.showToastError(R.string.wrong_start_time)
            false
        } else {
            dataInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.secondary_text_material_light))
            timeInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.secondary_text_material_light))
            timeET.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background)
            dateET.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background)
            true
        }
    }
}