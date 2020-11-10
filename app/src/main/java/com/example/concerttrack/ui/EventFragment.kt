package com.example.concerttrack.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.concerttrack.R
import com.example.concerttrack.dialogs.DeleteEventDialog
import com.example.concerttrack.models.Artist
import com.example.concerttrack.models.Event
import com.example.concerttrack.util.Constants
import com.example.concerttrack.util.Constants.Companion.COARSE_LOCATION
import com.example.concerttrack.util.Constants.Companion.DEFAULT_ZOOM
import com.example.concerttrack.util.Constants.Companion.FINE_LOCATION
import com.example.concerttrack.util.Constants.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.example.concerttrack.viewmodel.ArtistSettingsViewModel
import com.example.concerttrack.viewmodel.EventSettingsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.event_fragment.*
import kotlinx.android.synthetic.main.event_fragment.currentLocationBtn
import kotlinx.android.synthetic.main.event_fragment.placeAddressInput
import kotlinx.android.synthetic.main.event_fragment.placeLocationBtn
import java.text.SimpleDateFormat

class EventFragment: Fragment(R.layout.event_fragment), OnMapReadyCallback, DeleteEventDialog.DeleteEventDialogListener {

    private val eventSettingsViewModel: EventSettingsViewModel by lazy {
        ViewModelProvider(this).get(EventSettingsViewModel::class.java) }

    lateinit var event: Event
    lateinit var artist: Artist

    private var mContext: Context? = null

    var mLocationPermissionsGranted = false
    var mMap: GoogleMap? = null
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        event = arguments?.getSerializable("event") as Event

        if(arguments?.getBoolean("isArtistEvent")!!) {
            interestedBtn.visibility = View.GONE
            takePartBtn.visibility = View.GONE

            artist = ArtistMainPage.artist!!
            artistInput.text = artist.name

            ticketLinkBtn.visibility = View.GONE

        } else {
            deleteEventBtn.visibility = View.GONE
            editEventBtn.visibility = View.GONE
            setLayoutParams()
        }

        if(!arguments?.getBoolean("isPastEvent")!!) {
            pastEventInfo.visibility = View.GONE
        } else  {
            deleteEventBtn.visibility = View.GONE
            editEventBtn.visibility = View.GONE
            setLayoutParams()
        }
        setTextData()

        getLocationPermission()

        deleteEventBtn.setOnClickListener {
            openDeleteDialog()
        }

        editEventBtn.setOnClickListener {
            val intent = Intent(activity?.applicationContext,ArtistEventSettingsActivity::class.java).apply {
                putExtra("event",event)
            }
            startActivity(intent)
        }

        if(event.ticketsLink!="") {
            ticketLinkBtn.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(event.ticketsLink))
                startActivity(browserIntent)
            }
        }

    }

    private fun openDeleteDialog() {
        val deleteEventDialog = DeleteEventDialog(this)
        deleteEventDialog.show(fragmentManager!!,"delete")
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
            moveCamera(LatLng(
                event.placeLat,
                event.placeLng),
                DEFAULT_ZOOM, event.placeAddress)

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
        currentLocationBtn. setOnClickListener {
            getDeviceLocation()
        }

        placeLocationBtn.setOnClickListener {
            moveCamera(LatLng(
                event.placeLat,
                event.placeLng),
                DEFAULT_ZOOM, event.placeAddress)
        }
    }

    private fun getLocationPermission() {
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION)

        if(ContextCompat.checkSelfPermission(mContext!!,
                FINE_LOCATION
            )== PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(mContext!!,
                    COARSE_LOCATION
                )== PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true
                initMap()
            }
            else {
                ActivityCompat.requestPermissions(activity!!,permissions,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
        else {
            ActivityCompat.requestPermissions(activity!!,permissions,
               LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext!!)

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
                        Toast.makeText(activity,"unable to get current location", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } catch(e: SecurityException) {
            Toast.makeText(activity,e.message, Toast.LENGTH_LONG).show()
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

    private fun initMap() {
        val mapFragment: SupportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setTextData() {
        shortDescInput.text = event.shortDescription
        placeNameInput.text = event.placeName
        placeAddressInput.text = event.placeAddress
        eventName.text = event.header

        whenInput.text = event.startDateTime
    }

    private fun setLayoutParams() {
        val iconParams: ConstraintLayout.LayoutParams = eventIcon.layoutParams as ConstraintLayout.LayoutParams
        iconParams.setMargins(16,100,0,0)
        eventIcon.layoutParams = iconParams

        val nameParams: ConstraintLayout.LayoutParams = eventName.layoutParams as ConstraintLayout.LayoutParams
        nameParams.setMargins(8,120,0,0)
        eventName.layoutParams = nameParams

    }

    //deleting event
    override fun onYesClicked() {
        eventSettingsViewModel.deleteArtistEvent(event)
        activity?.finish()
        startActivity(Intent(mContext,ArtistMainPage::class.java))
    }
}


