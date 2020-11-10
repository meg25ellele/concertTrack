package com.example.concerttrack.repository

import android.app.Application
import android.util.Log
import com.example.concerttrack.models.Artist
import com.example.concerttrack.models.Event

import com.example.concerttrack.models.MusicGenre
import com.example.concerttrack.models.User
import com.example.concerttrack.util.Constants
import com.example.concerttrack.util.Resource
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.*


class CloudFirestoreRepository(private val application: Application) {

    private var firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun retrieveMusicGenres(myGenresString: String? = null): Resource<MutableList<MusicGenre>> {
        val musicGenresList = mutableListOf<MusicGenre>()
        val  querySnapshot = firebaseFirestore.collection("musicGenres").get().await()

        for(document in querySnapshot) {
            val genreName = document.getString("name")

            if (myGenresString != null) {
                if (myGenresString.contains(genreName!!)) {
                    musicGenresList.add(MusicGenre(genreName, true))
                } else {
                    musicGenresList.add(MusicGenre(genreName!!))
                }
            } else {
                musicGenresList.add(MusicGenre(genreName!!))
            }
        }

        return Resource.Success(musicGenresList)
    }

    suspend fun addNewUser(newUser: User): Resource<Boolean> {
       val data = hashMapOf<String,Any>(
           "email" to newUser.email,
           "name" to newUser.name
       )
        firebaseFirestore.collection("users").document(newUser.id).set(data).await()

        return Resource.Success(true)
    }

    suspend fun findUser(email:String):Resource<Boolean> {
        val querySnapshot = firebaseFirestore.collection("users")
            .whereEqualTo("email",email).get().await()

        Log.i("query",querySnapshot.documents.toString())

        return if(querySnapshot.documents.isEmpty()) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }

    suspend fun addNewArtist(newArtist: Artist): Resource<Boolean> {
        val data = hashMapOf<String,Any?>(
            "email" to newArtist.email,
            "name" to newArtist.name,
            "description" to newArtist.description,
            "facebookLink" to newArtist.facebookLink,
            "youtubeLink" to newArtist.youtubeLink,
            "spotifyLink" to newArtist.spotifyLink,
            "myGenres" to newArtist.myGenres
        )
        firebaseFirestore.collection("artists").document(newArtist.id).set(data).await()
        return Resource.Success(true)
    }


    suspend fun findArtist(email: String): Resource<Boolean> {
        val querySnapshot = firebaseFirestore.collection("artists")
            .whereEqualTo("email",email).get().await()

        return if(querySnapshot.documents.isEmpty()) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }

    suspend fun findArtistByName(name: String) : Resource<Boolean> {
        val querySnapshot = firebaseFirestore.collection("artists")
            .whereEqualTo("name",name).get().await()

        return if(querySnapshot.documents.isEmpty()) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }

    suspend fun getArtist(email: String): Resource<Pair<DocumentReference,Artist?>> {
        val querySnapshot = firebaseFirestore.collection("artists")
            .whereEqualTo("email",email).get().await()

        val document = querySnapshot.documents.first()

        val artist = Artist(document.id,
                            document.getString("email")!!,
                            document.getString("name")!!,
                            document.getString("description")!!,
                            document.getString("facebookLink")!!,
                            document.getString("youtubeLink")!!,
                            document.getString("spotifyLink")!!,
                            document.get("myGenres") as List<String>?)

        return Resource.Success(Pair(document.reference,artist))
    }

    suspend fun updateArtistData(id: String, newDataMap: Map<String,Any>): Resource<Boolean> {
        val artistQuery = firebaseFirestore.collection("artists").document(id).get().await()

        if(artistQuery.exists()) {
            firebaseFirestore.collection("artists").document(id).set(
                        newDataMap,
                        SetOptions.merge()
                    ).await()

        } else {
            return Resource.Success(false)
        }
        return Resource.Success(true)
    }

    suspend fun updateEventData(oldEvent:Event,newEvent: Event): Resource<Boolean> {
        val artistReference = firebaseFirestore.document(oldEvent.artistReferencePath)
        //oldData
        val placeGeoPointOld = GeoPoint(oldEvent.placeLat,oldEvent.placeLng)

        val locationMapOld = mapOf<String,Any>( "placeAddress" to oldEvent.placeAddress,
            "placeLatLng" to placeGeoPointOld,
            "placeName" to oldEvent.placeName)

        val parsedDateOld = ZonedDateTime.parse(oldEvent.startDateTime, Constants.DATE_TIME_FORMATTER)
        val oldDate = Date.from(parsedDateOld.toInstant())

        val eventQuery = firebaseFirestore.collection("events")
            .whereEqualTo("artist",artistReference)
            .whereEqualTo("header",oldEvent.header)
            .whereEqualTo("shortDescription",oldEvent.shortDescription)
            .whereEqualTo("startDateTime",Timestamp(oldDate))
            .whereEqualTo("ticketsLink", oldEvent.ticketsLink)
            .whereEqualTo("location",locationMapOld)
            .get().await()


        Log.i("error",eventQuery.toString())
        //newData
        val placeGeoPointNew = GeoPoint(newEvent.placeLat,newEvent.placeLng)

        val locationMapNew = mapOf<String,Any>( "placeAddress" to newEvent.placeAddress,
            "placeLatLng" to placeGeoPointNew,
            "placeName" to newEvent.placeName)

        val parsedDateNew = ZonedDateTime.parse(newEvent.startDateTime, Constants.DATE_TIME_FORMATTER)
        val newDate = Date.from(parsedDateNew.toInstant())


        val data = hashMapOf<String,Any>(
            "header" to newEvent.header,
            "startDateTime" to Timestamp(newDate),
            "shortDescription" to newEvent.shortDescription,
            "ticketsLink" to newEvent.ticketsLink,
            "location" to locationMapNew,
            "artist" to artistReference
        )

        return if(eventQuery.documents.isNotEmpty()) {
            for(document in eventQuery) {
                firebaseFirestore.collection("events").document(document.id).set(
                    data,
                    SetOptions.merge()
                ).await()
            }
            Resource.Success(true)
        } else {
            Resource.Success(false)
        }
    }


    suspend fun addNewEvent(newEvent: Event): Resource<Boolean>{
        val artist = firebaseFirestore.document(newEvent.artistReferencePath)

        val placeGeoPoint = GeoPoint(newEvent.placeLat,newEvent.placeLng)
        val parsedDate = ZonedDateTime.parse(newEvent.startDateTime, Constants.DATE_TIME_FORMATTER)
        val date = Date.from(parsedDate.toInstant())



        val data = hashMapOf<String,Any>(
            "header" to newEvent.header,
            "startDateTime" to Timestamp(date),
            "shortDescription" to newEvent.shortDescription,
            "ticketsLink" to newEvent.ticketsLink,
            "location" to hashMapOf<String,Any>(
                "placeName" to newEvent.placeName,
                "placeAddress" to newEvent.placeAddress,
                "placeLatLng" to placeGeoPoint
            ),
            "artist" to artist
        )
        firebaseFirestore.collection("events").add(data).await()
        return Resource.Success(true)
    }

    suspend fun deleteEvent(event: Event): Resource<Boolean> {
        val artist = firebaseFirestore.document(event.artistReferencePath)

        val placeGeoPoint = GeoPoint(event.placeLat,event.placeLng)


        val locationMap = mapOf<String,Any>( "placeAddress" to event.placeAddress,
                                        "placeLatLng" to placeGeoPoint,
                                        "placeName" to event.placeName)

        val parsedDate = ZonedDateTime.parse(event.startDateTime, Constants.DATE_TIME_FORMATTER)
        val date = Date.from(parsedDate.toInstant())


        val eventQuery = firebaseFirestore.collection("events")
            .whereEqualTo("artist",artist)
            .whereEqualTo("header",event.header)
            .whereEqualTo("shortDescription",event.shortDescription)
            .whereEqualTo("startDateTime",Timestamp(date))
            .whereEqualTo("ticketsLink", event.ticketsLink)
            .whereEqualTo("location",locationMap)
            .get().await()

        return if(eventQuery.documents.isNotEmpty()) {
            for(document in eventQuery) {
                firebaseFirestore.collection("events").document(document.id).delete().await()
            }

            Resource.Success(true)
        } else {
            Resource.Success(false)
        }
    }

    suspend fun retrieveArtistComingEvents(artist: DocumentReference): Resource.Success<MutableList<Event>> {
        val artistEvents = mutableListOf<Event>()

        val now = Date.from(ZonedDateTime.now().toInstant())

        val querySnapshot = firebaseFirestore.collection("events")
            .whereEqualTo("artist",artist)
            .whereGreaterThan("startDateTime",now)
            .get().await()



        for(document in querySnapshot) {

            val locationMap = document.get("location") as Map<String, Any>
            val placeName = locationMap["placeName"].toString()
            val placeAddress = locationMap["placeAddress"].toString()
            val placeGeoPoint = locationMap["placeLatLng"] as GeoPoint

            val formatter = SimpleDateFormat(Constants.DATE_TIME_FORMAT)
            val startDateTime =  formatter.format(document.getTimestamp("startDateTime")!!.toDate())


            val artistEvent = Event(document.getString("header")!!,
                startDateTime,
                document.getString("shortDescription")!!,
                document.getString("ticketsLink")!!,
                placeName,placeAddress,placeGeoPoint.latitude,placeGeoPoint.longitude,
                (document.get("artist") as DocumentReference).path)

            artistEvents.add(artistEvent)
            }
        return Resource.Success(artistEvents)
    }

    suspend fun retrieveArtistPastEvents(artist: DocumentReference): Resource.Success<MutableList<Event>> {
        val artistEvents = mutableListOf<Event>()

        val now = Date.from(ZonedDateTime.now().toInstant())

        val querySnapshot = firebaseFirestore.collection("events")
            .whereEqualTo("artist",artist)
            .whereLessThan("startDateTime",now)
            .get().await()


        for(document in querySnapshot) {
            Log.i("document",document.toString())
            val locationMap = document.get("location") as Map<String, Any>
            val placeName = locationMap["placeName"].toString()
            val placeAddress = locationMap["placeAddress"].toString()
            val placeGeoPoint = locationMap["placeLatLng"] as GeoPoint


            val formatter = SimpleDateFormat(Constants.DATE_TIME_FORMAT)
            val startDateTime =  formatter.format(document.getTimestamp("startDateTime")!!.toDate())


            val artistEvent = Event(document.getString("header")!!,
                startDateTime,
                document.getString("shortDescription")!!,
                document.getString("ticketsLink")!!,
                placeName,placeAddress,placeGeoPoint.latitude,placeGeoPoint.longitude,
                (document.get("artist") as DocumentReference).path)

            artistEvents.add(artistEvent)
        }
        return Resource.Success(artistEvents)
    }

 }

