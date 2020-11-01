package com.example.concerttrack.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.System.DATE_FORMAT
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.concerttrack.R
import com.example.concerttrack.util.Constants.Companion.DATE_TIME_FORMAT
import com.example.concerttrack.util.content
import com.example.concerttrack.util.showToastError
import kotlinx.android.synthetic.main.activity_fan_register.*
import kotlinx.android.synthetic.main.add_event_first_fragment.*
import kotlinx.android.synthetic.main.artist_panel_fragment.*
import kotlinx.android.synthetic.main.fragment_artist_register_first.*
import kotlinx.android.synthetic.main.fragment_artist_register_first.text_input_userName
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AddEventFirstFragment:Fragment(R.layout.add_event_first_fragment){


    private var mContext: Context? = null



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exitBtn.setOnClickListener {
            startActivity(Intent(activity,ArtistMainPage::class.java))
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
                getString(R.string.startDate) to datePT.text.toString(), getString(R.string.startTime) to timePT.text.toString(),
                getString(R.string.description) to eventDescET.text.toString(),
                getString(R.string.ticketsLink) to ticketsLink.text.toString())
                view.findNavController().navigate(R.id.action_addEventFirstFragment_to_addEventSecondFragment)
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun showCalendarAndPickDate() {
        val cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, day)
            val date = cal.time
            val formater  = SimpleDateFormat("dd-MM-yyyy")
            datePT.text = formater.format(date)
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
            val date = cal.time
            val formater  = SimpleDateFormat("HH:mm")
            timePT.text = formater.format(date)
        }

        TimePickerDialog(
            mContext!!,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

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

    private fun validateHeader(): Boolean {
        val isEmpty: Boolean = text_input_event_header.editText?.content()?.isEmpty() ?: true

        return when {
            isEmpty -> {
                text_input_event_header.error = getString(R.string.notAllowedEmptyField)
                false
            }
            text_input_event_header.editText?.length()!! > 15 -> {
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
        return when {
            datePT.text.toString()=="dd-MM-yyyy" -> {
                dataInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.red))
                datePT.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background_red)
                false
            }
            else ->  {
                dataInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.secondary_text_material_light))
                datePT.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background)
                true
            }
        }
    }

    private fun validateTime(): Boolean {
        return when {
            timePT.text.toString()=="HH:mm" -> {
                timeInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.red))
                timePT.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background_red)
                false
            }
            else ->  {
                timeInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.secondary_text_material_light))
                timePT.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background)
                true
            }
        }
    }
    private fun validateDesc():Boolean {
        val isEmpty: Boolean = eventDescET.text.isEmpty()

        return when {
            isEmpty -> {
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
        val dateAndTime =datePT.text.toString() + " " + timePT.text.toString()
        val parsedDate = ZonedDateTime.parse(dateAndTime, formatterDateTime)

        return if(parsedDate.isBefore(ZonedDateTime.now())){
            dataInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.red))
            timeInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.red))
            timePT.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background_red)
            datePT.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background_red)
            this.showToastError(R.string.wrong_start_time)
            false
        } else {
            dataInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.secondary_text_material_light))
            timeInfo.setTextColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.secondary_text_material_light))
            timePT.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background)
            datePT.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.edit_text_background)
            true
        }

    }

    companion object{
        val formatterDateTime = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).withZone(ZoneId.systemDefault())
    }
}