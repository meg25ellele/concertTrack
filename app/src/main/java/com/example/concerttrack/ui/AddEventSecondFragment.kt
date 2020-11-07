package com.example.concerttrack.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.concerttrack.R
import com.example.concerttrack.util.Resource
import com.example.concerttrack.util.content
import com.example.concerttrack.util.showToastError
import com.example.concerttrack.util.showToastSuccess
import com.example.concerttrack.viewmodel.AddEventViewModel
import com.example.concerttrack.viewmodel.ArtistRegisterViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.add_event_second_fragment.*
import kotlinx.android.synthetic.main.add_event_second_fragment.backBtn
import java.io.IOException


class AddEventSecondFragment:Fragment(R.layout.add_event_second_fragment), OnMapReadyCallback {


    private val addEventViewModel: AddEventViewModel by lazy { ViewModelProvider(this).get(
        AddEventViewModel::class.java) }

    private var allControls: List<View> = listOf()

    private var mContext: Context? = null

    lateinit var header: String
    lateinit var startTime:String
    lateinit var startDate: String
    lateinit var description: String
    lateinit var ticketsLink: String

    var mLocationPermissionsGranted = false
    var mMap: GoogleMap? = null
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        header = arguments?.getString(getString(R.string.header)).toString()
        startTime = arguments?.getString(getString(R.string.startTime)).toString()
        startDate = arguments?.getString(getString(R.string.startDate)).toString()
        description = arguments?.getString(getString(R.string.description)).toString()
        ticketsLink = arguments?.getString(getString(R.string.ticketsLink)).toString()

        backBtn.setOnClickListener {
            view.findNavController().popBackStack()
        }

        getLocationPermission()

        allControls = listOf(backBtn,text_input_placeName,currentLocationBtn,addEventBtn,placeLocationBtn)


        addEventViewModel.successfullyAddedEvent.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    hideSpinnerAndEnableControls()
                    this.showToastSuccess(R.string.newEventAddedSuccessfully)
                    startActivity(Intent(activity,ArtistMainPage::class.java))
                    activity?.finish()

                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    showToastError(R.string.newEventAddedError)
                }
            }
        })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
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
            getDeviceLocation()

            if (ActivityCompat.checkSelfPermission(
                    mContext!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    mContext!!,
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
        placeLocationBtn.visibility = View.GONE
        placeAddressInfo.text = resources.getString(R.string.empty)
        placeAddressInput.text = resources.getString(R.string.empty)

        searchPlaceET.setOnEditorActionListener { v, actionId, keyEvent ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                    keyEvent.action == KeyEvent.ACTION_DOWN ||
                        keyEvent.action == KeyEvent.KEYCODE_ENTER) {
                geoLocate()
            }
            false
        }

        searchPlaceET.addTextChangedListener {
            relLayout1.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.white_border)

        }
        placeNameET.addTextChangedListener {
            text_input_placeName.error = null
        }

        currentLocationBtn. setOnClickListener {
            getDeviceLocation()
        }

        placeLocationBtn.setOnClickListener {
            moveCamera(LatLng(eventAddress!!.latitude,eventAddress!!.longitude), DEFAULT_ZOOM, eventAddress!!.getAddressLine(0))
        }

        addEventBtn.setOnClickListener {
            if(areInputValid()) {
                addEventViewModel.addNewEvent(header,startTime,startDate,description,ticketsLink,ArtistMainPage.artistReference!!,
                    placeNameET.text.toString(),placeAddressInput.text.toString(),
                    GeoPoint(eventAddress!!.latitude, eventAddress!!.longitude))
            }
        }
    }

    private fun geoLocate() {
        val searchString = searchPlaceET.text.toString()
        val geoCoder = Geocoder(activity)
        var resultsList: MutableList<Address> = mutableListOf<Address>()

        try{
            resultsList = geoCoder.getFromLocationName(searchString,1)
        }catch (e: IOException) {
            Toast.makeText(activity,e.message,Toast.LENGTH_LONG).show()
        }

        if(resultsList.size >0) {
            val address = resultsList[0]
            moveCamera(LatLng(address.latitude,address.longitude), DEFAULT_ZOOM, address.getAddressLine(0))
            eventAddress = address
            placeLocationBtn.visibility = View.VISIBLE
            placeAddressInfo.text = resources.getString(R.string.addressInfo)
            placeAddressInput.text = address.getAddressLine(0)
        } else  {
            this.showToastError(R.string.noPlaceFound)
        }
    }


    private fun getLocationPermission() {
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                                        android.Manifest.permission.ACCESS_COARSE_LOCATION)

        if(ContextCompat.checkSelfPermission(mContext!!, FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(mContext!!, COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true
                initMap()
            }
            else {
                ActivityCompat.requestPermissions(activity!!,permissions,LOCATION_PERMISSION_REQUEST_CODE)
            }
        }
        else {
            ActivityCompat.requestPermissions(activity!!,permissions,LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun initMap() {
        val mapFragment: SupportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext!!)

        try{
            if(mLocationPermissionsGranted) {
                val location = mFusedLocationProviderClient.lastLocation
                location.addOnCompleteListener {
                    if(it.isSuccessful) {
                        val currentLocation = it.result
                        moveCamera(LatLng(currentLocation.latitude,currentLocation.longitude),DEFAULT_ZOOM,
                        "My Location")
                    } else {
                        Toast.makeText(activity,"unable to get current location",Toast.LENGTH_LONG).show()
                    }
                }
            }

        } catch(e: SecurityException) {
            Toast.makeText(activity,e.message,Toast.LENGTH_LONG).show()
        }
    }

    private fun moveCamera(latLng: LatLng,zoom: Float, title: String) {
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom))

        if(!title.equals("My Location")) {
            mMap!!.clear()
            val options = MarkerOptions()
                .position(latLng)
                .title(title)
            mMap!!.addMarker(options)
        }
    }

    private fun areInputValid():Boolean {
        val isPlaceName = validatePlaceName()
        val isPlaceAddress = validatePlaceAddress()

        return isPlaceName && isPlaceAddress
    }

    private fun validatePlaceName(): Boolean {
        val isEmpty: Boolean = text_input_placeName.editText?.content()?.isEmpty() ?: true

        return when {
            isEmpty -> {
                text_input_placeName.error = getString(R.string.notAllowedEmptyField)
                false
            }
            text_input_placeName.editText?.length()!! > 40 -> {
                text_input_placeName.error = getString(R.string.tooLongUserName)
                false
            }
            else -> {
                text_input_placeName.error = null
                true
            }
        }
    }

    private fun validatePlaceAddress():Boolean {
        return if(placeAddressInput.text == resources.getString(R.string.empty)) {
            relLayout1.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.red_border)
            false
        } else  {
            relLayout1.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.white_border)
            true
        }
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
        val FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION
        val COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION
        val LOCATION_PERMISSION_REQUEST_CODE = 1234
        val DEFAULT_ZOOM = 15f

        var eventAddress: Address? = null
    }

}