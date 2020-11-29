package com.example.concerttrack.ui

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.concerttrack.R
import com.example.concerttrack.models.Event
import com.example.concerttrack.util.*
import com.example.concerttrack.util.Constants.Companion.COARSE_LOCATION
import com.example.concerttrack.util.Constants.Companion.DEFAULT_ZOOM
import com.example.concerttrack.util.Constants.Companion.FINE_LOCATION
import com.example.concerttrack.util.Constants.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.example.concerttrack.viewmodel.EventSettingsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_artist_event_settings.*
import kotlinx.android.synthetic.main.activity_artist_event_settings.currentLocationBtn
import kotlinx.android.synthetic.main.activity_artist_event_settings.dateBtn
import kotlinx.android.synthetic.main.activity_artist_event_settings.datePT
import kotlinx.android.synthetic.main.activity_artist_event_settings.eventDescET
import kotlinx.android.synthetic.main.activity_artist_event_settings.eventHeaderET
import kotlinx.android.synthetic.main.activity_artist_event_settings.exitBtn
import kotlinx.android.synthetic.main.activity_artist_event_settings.placeAddressInput
import kotlinx.android.synthetic.main.activity_artist_event_settings.placeLocationBtn
import kotlinx.android.synthetic.main.activity_artist_event_settings.placeNameET
import kotlinx.android.synthetic.main.activity_artist_event_settings.progressBar
import kotlinx.android.synthetic.main.activity_artist_event_settings.saveChangesBtn
import kotlinx.android.synthetic.main.activity_artist_event_settings.searchPlaceET
import kotlinx.android.synthetic.main.activity_artist_event_settings.text_input_event_header
import kotlinx.android.synthetic.main.activity_artist_event_settings.text_input_placeName
import kotlinx.android.synthetic.main.activity_artist_event_settings.ticketsLink
import kotlinx.android.synthetic.main.activity_artist_event_settings.timeBtn
import kotlinx.android.synthetic.main.activity_artist_event_settings.timePT
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ArtistEventSettingsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val eventSettingsViewModel: EventSettingsViewModel by lazy {
        ViewModelProvider(this).get(EventSettingsViewModel::class.java) }

    lateinit var event: Event

    var mLocationPermissionsGranted = false
    var mMap: GoogleMap? = null
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    private var allControls: List<View> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_event_settings)

        allControls = listOf(exitBtn,text_input_event_header,dateBtn,timeBtn,eventDescET,ticketsLink,
            text_input_placeName,currentLocationBtn,placeLocationBtn,saveChangesBtn,searchPlaceET)

        event = intent.getSerializableExtra("event") as Event

        val geoCoder = Geocoder(this)
        eventAddress = geoCoder.getFromLocation(event.placeLat,event.placeLng,1)[0]

        loadData()
        getLocationPermission()

        placeNameET.addTextChangedListener {
            text_input_placeName.error = null
        }

        eventDescET.addTextChangedListener {
            emptyDescMsg.visibility = View.INVISIBLE
            eventDescInfo.setTextColor(ContextCompat.getColor(this,R.color.secondary_text_material_light))
            eventDescET.background = ContextCompat.getDrawable(this,R.drawable.edit_text_background)
        }


        eventHeaderET.addTextChangedListener {
            text_input_event_header.error = null
        }

        exitBtn.setOnClickListener {
            finish()
        }

        dateBtn.setOnClickListener {
            showCalendarAndPickDate()
        }

        timeBtn.setOnClickListener{
            showClockAndPickTime()
        }

        saveChangesBtn.setOnClickListener {
            if(isDataEdited()) {
                if(areInputValid()) {
                    eventSettingsViewModel.updateEventData(event,getNewData())
                }
            } else {
                this.showToastInfo(R.string.nothingToEditEvent)
            }
        }


        eventSettingsViewModel.successfullyUpdatedEvent.observe(this,Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    if(it.data){
                        this.showToastSuccess(R.string.successUpdatingData)
                        hideSpinnerAndEnableControls()
                        startActivity(Intent(this,ArtistMainPageActivity::class.java))
                        finish()
                    }else {
                        hideSpinnerAndEnableControls()
                        this.showToastError(R.string.unknownUpdatingEventError)
                    }
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    Log.i("error",it.throwable.message)
                    this.showToastError(R.string.unknownUpdatingEventError)
                }
            }
        })
    }

    private fun getNewData(): Event {

        val header = eventHeaderET.text.toString()
        val starDateTime = datePT.text.toString() + " " + timePT.text.toString()
        val shortDescription = eventDescET.text.toString()
        val ticketsLink = ticketsLink.text.toString()
        val placeName = placeNameET.text.toString()
        val placeAddress = placeAddressInput.text.toString().replace(",Polska","")
        val placeLat = eventAddress!!.latitude
        val placeLng = eventAddress!!.longitude

        return Event(header,starDateTime,shortDescription,ticketsLink,placeName,placeAddress,placeLat,placeLng,event.artistReferencePath)
    }

    private fun areInputValid():Boolean {
        val isHeaderOk = validateHeader()
        val isDescOk = validateDesc()
        val isPlaceName = validatePlaceName()

        return isHeaderOk && isDescOk && isPlaceName

    }

    private fun validateHeader(): Boolean {
        val headerEditText = text_input_event_header.editText?.content().toString()

        return when {
            FormValidators.isInputEmpty(headerEditText) -> {
                text_input_event_header.error = getString(R.string.notAllowedEmptyField)
                false
            }
            FormValidators.isHeaderTooLong(headerEditText) -> {
                text_input_event_header.error = getString(R.string.tooLongHeader)
                false
            }
            else -> {
                text_input_event_header.error = null
                true
            }
        }
    }

    private fun validateDesc():Boolean {
        val descriptionEditText = eventDescET.text.toString()

        return when {
            FormValidators.isInputEmpty(descriptionEditText) -> {
                eventDescInfo.setTextColor(ContextCompat.getColor(this,R.color.red))
                emptyDescMsg.visibility = View.VISIBLE
                eventDescET.background = ContextCompat.getDrawable(this,R.drawable.edit_text_background_red)
                false
            }
            else -> {
                emptyDescMsg.visibility = View.INVISIBLE
                eventDescInfo.setTextColor(ContextCompat.getColor(this,R.color.secondary_text_material_light))
                eventDescET.background = ContextCompat.getDrawable(this,R.drawable.edit_text_background)
                true
            }
        }
    }

    private fun validatePlaceName(): Boolean {
        val placeNameEditText = text_input_placeName.editText?.content().toString()

        return when {
            FormValidators.isInputEmpty(placeNameEditText) -> {
                text_input_placeName.error = getString(R.string.notAllowedEmptyField)
                false
            }
            FormValidators.isPlaceNameTooLong(placeNameEditText) -> {
                text_input_placeName.error = getString(R.string.tooLongPlaceName)
                false
            }
            else -> {
                text_input_placeName.error = null
                true
            }
        }
    }

    private fun isDataEdited():Boolean {
        val spitedDateTime = event.startDateTime.split(" ")

       return eventHeaderET.text.toString() != event.header ||
               datePT.text != spitedDateTime[0] ||
               timePT.text != spitedDateTime[1] ||
               eventDescET.text.toString() != event.shortDescription ||
               ticketsLink.text.toString() != event.ticketsLink ||
               placeNameET.text.toString() != event.placeName ||
               placeAddressInput.text != event.placeAddress
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        mLocationPermissionsGranted = false

        when(requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if(grantResults.isNotEmpty()) {
                    for(result in grantResults) {
                        if(result != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false
                            return
                        }
                    }
                    mLocationPermissionsGranted = true
                    initMap()
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap

        if(mLocationPermissionsGranted) {
            moveCamera(
                LatLng(
                event.placeLat,
                event.placeLng),
                DEFAULT_ZOOM, event.placeAddress)

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            mMap!!.isMyLocationEnabled = true
            mMap!!.uiSettings.isMyLocationButtonEnabled = false
            init()
        }
    }

    private fun init() {
        currentLocationBtn. setOnClickListener {
            getDeviceLocation()
        }

        searchPlaceET.setOnEditorActionListener { v, actionId, keyEvent ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                keyEvent.action == KeyEvent.ACTION_DOWN ||
                keyEvent.action == KeyEvent.KEYCODE_ENTER) {
                geoLocate()
            }
            false
        }

        placeLocationBtn.setOnClickListener {
            moveCamera(
                LatLng(
                eventAddress!!.latitude,
                eventAddress!!.longitude),
                DEFAULT_ZOOM, event.placeAddress)
        }
    }

    private fun showCalendarAndPickDate() {
        val cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, day)
            val date = cal.time
            val formatter  = SimpleDateFormat(Constants.DATE_FORMAT)
            datePT.text = formatter.format(date)
        }

        DatePickerDialog(
            this,
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
            val formater  = SimpleDateFormat(Constants.TIME_FORMAT)
            timePT.text = formater.format(time)
        }

        TimePickerDialog(
            this,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun geoLocate() {
        val searchString = searchPlaceET.text.toString()
        val geoCoder = Geocoder(this)
        var resultsList: MutableList<Address> = mutableListOf<Address>()

        try{
            resultsList = geoCoder.getFromLocationName(searchString,1)
        }catch (e: IOException) {
            Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()
        }

        if(resultsList.size >0) {
            val address = resultsList[0]
            moveCamera(LatLng(address.latitude,address.longitude), DEFAULT_ZOOM, address.getAddressLine(0))
            eventAddress = address
            placeLocationBtn.visibility = View.VISIBLE
            placeAddressInput.text = address.getAddressLine(0)
        } else  {
            this.showToastError(R.string.noPlaceFound)
        }
    }

    private fun getLocationPermission() {
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION)

        if(ContextCompat.checkSelfPermission(this,
                FINE_LOCATION
            )== PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this,
                    COARSE_LOCATION
                )== PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true
                initMap()
            }
            else {
                ActivityCompat.requestPermissions(this,permissions,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
        else {
            ActivityCompat.requestPermissions(this,permissions,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        try{
            if(mLocationPermissionsGranted) {
                val location = mFusedLocationProviderClient.lastLocation
                location.addOnCompleteListener {
                    if(it.isSuccessful) {
                        val currentLocation = it.result
                        moveCamera(
                            LatLng(currentLocation.latitude,currentLocation.longitude),
                            DEFAULT_ZOOM,
                            "My Location")
                    } else {
                        Toast.makeText(this,"unable to get current location", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } catch(e: SecurityException) {
            Toast.makeText(this,e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun moveCamera(latLng: LatLng, zoom: Float, title: String) {
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom))

        if(!title.equals("My Location")) {
            mMap!!.clear()
            val options = MarkerOptions()
                .position(latLng)
                .title(title)
            mMap!!.addMarker(options)
        }
    }

    private fun initMap() {
        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun loadData() {
        eventHeaderET.setText(event.header)

        val spitedDateTime = event.startDateTime.split(" ")
        datePT.text = spitedDateTime[0]
        timePT.text = spitedDateTime[1]

        eventDescET.setText(event.shortDescription)
        ticketsLink.setText(event.ticketsLink)
        placeNameET.setText(event.placeName)
        placeAddressInput.text = event.placeAddress
    }

    private fun showSpinnerAndDisableControls() {
        progressBar.visibility = View.VISIBLE
        allControls.forEach { v -> v.isEnabled = false }
    }

    private  fun hideSpinnerAndEnableControls() {
        progressBar.visibility = View.GONE
        allControls.forEach { v -> v.isEnabled = true }
    }

    companion object {
        var eventAddress: Address? = null
    }
}