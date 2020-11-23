package com.example.concerttrack.repository

import android.app.Application
import android.util.Log
import com.example.concerttrack.models.Artist
import com.example.concerttrack.models.Event

import com.example.concerttrack.models.MusicGenre
import com.example.concerttrack.models.Fan
import com.example.concerttrack.util.Constants
import com.example.concerttrack.util.Resource
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

    suspend fun addNewFan(newFan: Fan): Resource<Boolean> {
       val data = hashMapOf<String,Any>(
           "email" to newFan.email,
           "name" to newFan.name,
           "favouritesArtists" to newFan.favouritesArtists,
           "interestedEvents" to newFan.interestedEvents,
           "myEvents" to newFan.myEvents
       )
        firebaseFirestore.collection("fans").document(newFan.id).set(data).await()

        return Resource.Success(true)
    }

    suspend fun findFan(email:String):Resource<Boolean> {
        val querySnapshot = firebaseFirestore.collection("fans")
            .whereEqualTo("email",email).get().await()

        Log.i("query",querySnapshot.documents.toString())

        return if(querySnapshot.documents.isEmpty()) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }

    suspend fun getFan(email:String): Resource<Fan> {
        val querySnapshot = firebaseFirestore.collection("fans")
            .whereEqualTo("email",email).get().await()

        val document = querySnapshot.documents.first()

        val favouritesArtists = mutableListOf<String>()
        val interestedEvents = mutableListOf<String>()
        val myEvents = mutableListOf<String>()


        for(artistReference in document.get("favouritesArtists") as List<DocumentReference>) {
            favouritesArtists.add(artistReference.path)
        }
        for(artistReference in document.get("interestedEvents") as List<DocumentReference>) {
            interestedEvents.add(artistReference.path)
        }
        for(artistReference in document.get("myEvents") as List<DocumentReference>) {
            myEvents.add(artistReference.path)
        }
        val fan = Fan(document.id,
                    document.getString("email")!!,
                    document.getString("name")!!,
                    interestedEvents,
                    favouritesArtists,
                    myEvents)

        return Resource.Success(fan)
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

    suspend fun retrieveArtistComingEvents(artist: Artist): Resource.Success<MutableList<Event>> {
        val artist = firebaseFirestore.document("artists/"+ artist.id)

        val artistEvents = mutableListOf<Event>()

        val now = Date.from(ZonedDateTime.now().toInstant())

        val querySnapshot = firebaseFirestore.collection("events")
            .whereEqualTo("artist",artist)
            .whereGreaterThan("startDateTime",now)
            .orderBy("startDateTime",Query.Direction.ASCENDING)
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
                (document.get("artist") as DocumentReference).path,
                document.id)

            artistEvents.add(artistEvent)
            }
        return Resource.Success(artistEvents)
    }

    suspend fun retrieveArtistPastEvents(artist: Artist): Resource.Success<MutableList<Event>> {
        val artist = firebaseFirestore.document("artists/"+ artist.id)

        val artistEvents = mutableListOf<Event>()

        val now = Date.from(ZonedDateTime.now().toInstant())

        val querySnapshot = firebaseFirestore.collection("events")
            .whereEqualTo("artist",artist)
            .whereLessThan("startDateTime",now)
            .orderBy("startDateTime",Query.Direction.DESCENDING)
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
                (document.get("artist") as DocumentReference).path,
                document.id)

            artistEvents.add(artistEvent)
        }
        return Resource.Success(artistEvents)
    }

    suspend fun retrieveComingEvents(): Resource.Success<MutableList<Event>> {
        val artistEvents = mutableListOf<Event>()


        val now = Date.from(ZonedDateTime.now().toInstant())

        val querySnapshot = firebaseFirestore.collection("events")
            .whereGreaterThan("startDateTime",now)
            .orderBy("startDateTime",Query.Direction.ASCENDING)
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
                (document.get("artist") as DocumentReference).path,
                document.id)

            artistEvents.add(artistEvent)
        }
        return Resource.Success(artistEvents)
    }

    suspend fun retrievePastEvents(): Resource.Success<MutableList<Event>> {
        val artistEvents = mutableListOf<Event>()

        val now = Date.from(ZonedDateTime.now().toInstant())

        val querySnapshot = firebaseFirestore.collection("events")
            .whereLessThan("startDateTime",now)
            .orderBy("startDateTime",Query.Direction.DESCENDING)
            .get().await()

        Log.i("events",querySnapshot.size().toString())

        for(document in querySnapshot) {

            Log.i("events",document.toString())

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
                (document.get("artist") as DocumentReference).path,
                document.id)

            artistEvents.add(artistEvent)
        }
        return Resource.Success(artistEvents)
    }

    suspend fun getArtistsMap():Resource<Map<String,Artist>> {
        val artistsMap = mutableMapOf<String,Artist>()

        val artists = firebaseFirestore.collection("artists").get().await()

        for(artistSnapshot in artists) {
            val artist = Artist(artistSnapshot.id,
                artistSnapshot.getString("email"),
                artistSnapshot.getString("name")!!,
                artistSnapshot.getString("description")!!,
                artistSnapshot.getString("facebookLink")!!,
                artistSnapshot.getString("youtubeLink")!!,
                artistSnapshot.getString("spotifyLink")!!,
                artistSnapshot.get("myGenres") as List<String>?)



            artistsMap.set("artists/" + artistSnapshot.id,artist)
        }
        return Resource.Success(artistsMap)
    }

    suspend fun addArtistToFavourites(fanID: String, artistID: String): Resource<Boolean> {
        val artist = firebaseFirestore.document("artists/"+ artistID)

        var fan = firebaseFirestore.collection("fans").document(fanID)

        fan.update("favouritesArtists",FieldValue.arrayUnion(artist)).await()
        return Resource.Success(true)
    }

    suspend fun removeArtistFromFavourites(fanID: String,artistID: String) : Resource<Boolean> {
        Log.i("artist","in")
        val artist = firebaseFirestore.document("artists/"+ artistID)

        var fan = firebaseFirestore.collection("fans").document(fanID)
        fan.update("favouritesArtists", FieldValue.arrayRemove(artist)).await()
        return Resource.Success(true)
    }

    suspend fun addEventToInterested(fanID: String,event: Event) : Resource<Boolean> {
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

        val event = eventQuery.documents.first().reference

        var fan = firebaseFirestore.collection("fans").document(fanID)
        fan.update("interestedEvents",FieldValue.arrayUnion(event)).await()
        return Resource.Success(true)
    }

    suspend fun removeEventFromInterested(fanID: String,event: Event) : Resource<Boolean> {
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

        val event = eventQuery.documents.first().reference

        var fan = firebaseFirestore.collection("fans").document(fanID)
        fan.update("interestedEvents",FieldValue.arrayRemove(event)).await()
        return Resource.Success(true)
    }

    suspend fun addEventToMine(fanID: String,event: Event) : Resource<Boolean> {
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

        val event = eventQuery.documents.first().reference

        var fan = firebaseFirestore.collection("fans").document(fanID)
        fan.update("myEvents",FieldValue.arrayUnion(event)).await()
        return Resource.Success(true)
    }

    suspend fun removeEventFromMine(fanID: String,event: Event) : Resource<Boolean> {
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

        val event = eventQuery.documents.first().reference

        var fan = firebaseFirestore.collection("fans").document(fanID)
        fan.update("myEvents",FieldValue.arrayRemove(event)).await()
        return Resource.Success(true)
    }

    suspend fun retrieveFanEvents(fanID: String): Resource.Success<Pair<MutableList<Event>,MutableList<Event>>>{
        val fanComingEvents = mutableListOf<Event>()
        val fanPastEvents = mutableListOf<Event>()

        val fanDocumentReference = firebaseFirestore.collection("fans").document(fanID).get().await()
        val eventsReferences = fanDocumentReference.get("myEvents") as List<DocumentReference>

        for(eventReference in eventsReferences) {
            val document = firebaseFirestore.document(eventReference.path).get().await()
            if(!document.exists()) {
                var fan = firebaseFirestore.collection("fans").document(fanID)
                fan.update("myEvents",FieldValue.arrayRemove(eventReference)).await()
            } else {
                val locationMap = document.get("location") as Map<String, Any>
                val placeName = locationMap["placeName"].toString()
                val placeAddress = locationMap["placeAddress"].toString()
                val placeGeoPoint = locationMap["placeLatLng"] as GeoPoint


                val formatter = SimpleDateFormat(Constants.DATE_TIME_FORMAT)
                val startDateTime =  formatter.format(document.getTimestamp("startDateTime")!!.toDate())

                val fanEvent = Event(document.getString("header")!!,
                    startDateTime,
                    document.getString("shortDescription")!!,
                    document.getString("ticketsLink")!!,
                    placeName,placeAddress,placeGeoPoint.latitude,placeGeoPoint.longitude,
                    (document.get("artist") as DocumentReference).path,
                    document.id)

                val parsedDate = ZonedDateTime.parse(fanEvent.startDateTime, Constants.DATE_TIME_FORMATTER)
                if(parsedDate.isBefore(ZonedDateTime.now())) {
                    fanPastEvents.add(fanEvent)
                } else {
                    fanComingEvents.add(fanEvent)
                }

            }
        }
        fanPastEvents.sortWith(kotlin.Comparator { o1, o2 ->
            ZonedDateTime.parse(o2.startDateTime, Constants.DATE_TIME_FORMATTER).
            compareTo(ZonedDateTime.parse(o1.startDateTime, Constants.DATE_TIME_FORMATTER))})
        fanComingEvents.sortWith(kotlin.Comparator { o1, o2 ->
            ZonedDateTime.parse(o1.startDateTime, Constants.DATE_TIME_FORMATTER).
            compareTo(ZonedDateTime.parse(o2.startDateTime, Constants.DATE_TIME_FORMATTER)) })

        return Resource.Success(Pair(fanComingEvents,fanPastEvents))
    }

    suspend fun retrieveInterestedEvents(fanID: String): Resource.Success<Pair<MutableList<Event>,MutableList<Event>>> {
        val fanComingEvents = mutableListOf<Event>()
        val fanPastEvents = mutableListOf<Event>()

        val fanDocumentReference = firebaseFirestore.collection("fans").document(fanID).get().await()
        val eventsReferences = fanDocumentReference.get("interestedEvents") as List<DocumentReference>

        for(eventReference in eventsReferences) {
            val document = firebaseFirestore.document(eventReference.path).get().await()
            if(!document.exists()) {
                var fan = firebaseFirestore.collection("fans").document(fanID)
                fan.update("interestedEvents",FieldValue.arrayRemove(eventReference)).await()
            }
            else {
                val locationMap = document.get("location") as Map<String, Any>
                val placeName = locationMap["placeName"].toString()
                val placeAddress = locationMap["placeAddress"].toString()
                val placeGeoPoint = locationMap["placeLatLng"] as GeoPoint


                val formatter = SimpleDateFormat(Constants.DATE_TIME_FORMAT)
                val startDateTime =
                    formatter.format(document.getTimestamp("startDateTime")!!.toDate())

                val fanEvent = Event(
                    document.getString("header")!!,
                    startDateTime,
                    document.getString("shortDescription")!!,
                    document.getString("ticketsLink")!!,
                    placeName, placeAddress, placeGeoPoint.latitude, placeGeoPoint.longitude,
                    (document.get("artist") as DocumentReference).path,
                    document.id
                )

                val parsedDate =
                    ZonedDateTime.parse(fanEvent.startDateTime, Constants.DATE_TIME_FORMATTER)
                if (parsedDate.isBefore(ZonedDateTime.now())) {
                    fanPastEvents.add(fanEvent)
                } else {
                    fanComingEvents.add(fanEvent)
                }
            }
        }

        fanPastEvents.sortWith(kotlin.Comparator { o1, o2 ->
            ZonedDateTime.parse(o2.startDateTime, Constants.DATE_TIME_FORMATTER).
            compareTo(ZonedDateTime.parse(o1.startDateTime, Constants.DATE_TIME_FORMATTER))})
        fanComingEvents.sortWith(kotlin.Comparator { o1, o2 ->
            ZonedDateTime.parse(o1.startDateTime, Constants.DATE_TIME_FORMATTER).
            compareTo(ZonedDateTime.parse(o2.startDateTime, Constants.DATE_TIME_FORMATTER)) })

        return Resource.Success(Pair(fanComingEvents,fanPastEvents))
    }

    suspend fun retrieveFavouritesArtists(fanID: String): Resource.Success<MutableList<Artist>> {
        val fanArtists = mutableListOf<Artist>()

        val fanDocumentReference = firebaseFirestore.collection("fans").document(fanID).get().await()
        val artistsReferences = fanDocumentReference.get("favouritesArtists") as List<DocumentReference>

        for(artistReference in artistsReferences) {
            val document = firebaseFirestore.document(artistReference.path).get().await()
            if(!document.exists()) {
                var fan = firebaseFirestore.collection("fans").document(fanID)
                fan.update("favouritesArtists", FieldValue.arrayRemove(artistReference)).await()
            }
            val artist = Artist(document.id,
                document.getString("email"),
                document.getString("name")!!,
                document.getString("description")!!,
                document.getString("facebookLink")!!,
                document.getString("youtubeLink")!!,
                document.getString("spotifyLink")!!,
                document.get("myGenres") as List<String>?)
            
            fanArtists.add(artist)
        }
        return Resource.Success(fanArtists)
    }

    suspend fun searchForArtists(word: String): Resource<Map<String,Artist>> {
        val artistsMap = mutableMapOf<String,Artist>()

        //getArtistsResult
        val artistsDocuments = firebaseFirestore.collection("artists").get().await()

        for(artistSnapshot in artistsDocuments) {
            val artist = Artist(artistSnapshot.id,
                artistSnapshot.getString("email"),
                artistSnapshot.getString("name")!!,
                artistSnapshot.getString("description")!!,
                artistSnapshot.getString("facebookLink")!!,
                artistSnapshot.getString("youtubeLink")!!,
                artistSnapshot.getString("spotifyLink")!!,
                artistSnapshot.get("myGenres") as List<String>?)

            if(artist.getArtistString().contains(word)) {
                artistsMap[artist.id] = artist
            }
        }
        return Resource.Success(artistsMap)
    }

    suspend fun searchForEvents(word: String,artistsIdList: List<String>): Resource<Map<String,Any>> {
        val comingEventsList = mutableListOf<Event>()
        val pastEventsList = mutableListOf<Event>()

        val now = Date.from(ZonedDateTime.now().toInstant())

        //getComingEvents
        val querySnapshotComingEvents = firebaseFirestore.collection("events")
            .whereGreaterThan("startDateTime",now)
            .orderBy("startDateTime",Query.Direction.ASCENDING)
            .get().await()


        for(document in querySnapshotComingEvents) {
            val locationMap = document.get("location") as Map<String, Any>
            val placeName = locationMap["placeName"].toString()
            val placeAddress = locationMap["placeAddress"].toString()
            val placeGeoPoint = locationMap["placeLatLng"] as GeoPoint

            val formatter = SimpleDateFormat(Constants.DATE_TIME_FORMAT)
            val startDateTime =  formatter.format(document.getTimestamp("startDateTime")!!.toDate())

            val event = Event(document.getString("header")!!,
                startDateTime,
                document.getString("shortDescription")!!,
                document.getString("ticketsLink")!!,
                placeName,placeAddress,placeGeoPoint.latitude,placeGeoPoint.longitude,
                (document.get("artist") as DocumentReference).path,
                document.id)

            val artistID =   (document.get("artist") as DocumentReference).path.replace("artists/","")

            if(event.getEventString().contains(word) || artistsIdList.contains(artistID)) {
                comingEventsList.add(event)
            }
        }

        //getPastEvents
        val querySnapshotPastEvents = firebaseFirestore.collection("events")
            .whereLessThan("startDateTime",now)
            .orderBy("startDateTime",Query.Direction.DESCENDING)
            .get().await()


        for(document in querySnapshotPastEvents) {
            val locationMap = document.get("location") as Map<String, Any>
            val placeName = locationMap["placeName"].toString()
            val placeAddress = locationMap["placeAddress"].toString()
            val placeGeoPoint = locationMap["placeLatLng"] as GeoPoint

            val formatter = SimpleDateFormat(Constants.DATE_TIME_FORMAT)
            val startDateTime =  formatter.format(document.getTimestamp("startDateTime")!!.toDate())

            val event = Event(document.getString("header")!!,
                startDateTime,
                document.getString("shortDescription")!!,
                document.getString("ticketsLink")!!,
                placeName,placeAddress,placeGeoPoint.latitude,placeGeoPoint.longitude,
                (document.get("artist") as DocumentReference).path,
                document.id)

            val artistID =   (document.get("artist") as DocumentReference).path.replace("artists/","")

            if(event.getEventString().contains(word) || artistsIdList.contains(artistID)) {
                pastEventsList.add(event)
            }
        }

        return Resource.Success(mapOf("comingEventsList" to comingEventsList,
                                "pastEventsList" to pastEventsList))
    }

    suspend fun hasFanFavouritesArtists(fanID: String): Resource<Boolean> {
        val fanDocumentReference = firebaseFirestore.collection("fans").document(fanID).get().await()
        val artistsReferences = fanDocumentReference.get("favouritesArtists") as List<DocumentReference>

        return Resource.Success(artistsReferences.isNotEmpty())

    }

    suspend fun getFavouriteAndRecommendedArtists(fanID: String):Resource<Map<String,Any>> {
        val favArtistsMap = mutableMapOf<String,Artist>()
        val recommendedArtistsMap = mutableMapOf<String,Artist>()
        var favGenresList = mutableListOf<String>()

        val fanDocumentReference = firebaseFirestore.collection("fans").document(fanID).get().await()
        val artistsReferences = fanDocumentReference.get("favouritesArtists") as List<DocumentReference>

        for(artistReference in artistsReferences) {
            val document = firebaseFirestore.document(artistReference.path).get().await()
            if (!document.exists()) {
                var fan = firebaseFirestore.collection("fans").document(fanID)
                fan.update("favouritesArtists", FieldValue.arrayRemove(artistReference)).await()
            }
            val artist = Artist(
                document.id,
                document.getString("email"),
                document.getString("name")!!,
                document.getString("description")!!,
                document.getString("facebookLink")!!,
                document.getString("youtubeLink")!!,
                document.getString("spotifyLink")!!,
                document.get("myGenres") as List<String>?
            )

            if (document.get("myGenres") != null) {
                val myGenres = document.get("myGenres")!! as List<String>
                for (genre in myGenres) {
                    favGenresList.add(genre)
                }
            }

            favArtistsMap["artists/" + artist.id] = artist
        }

        val favGenresDistinct = (favGenresList as List<String>).distinct()

        val artists = firebaseFirestore.collection("artists").get().await()

        for(artistSnapshot in artists) {

            val artist = Artist(artistSnapshot.id,
                artistSnapshot.getString("email"),
                artistSnapshot.getString("name")!!,
                artistSnapshot.getString("description")!!,
                artistSnapshot.getString("facebookLink")!!,
                artistSnapshot.getString("youtubeLink")!!,
                artistSnapshot.getString("spotifyLink")!!,
                artistSnapshot.get("myGenres") as List<String>?)

            if(!favArtistsMap.contains("artists/" + artist.id)) {
                if (artistSnapshot.get("myGenres") != null) {
                    val myGenres = artistSnapshot.get("myGenres")!! as List<String>
                    for (favGenre in favGenresDistinct) {
                        if(myGenres.contains(favGenre)) {
                            recommendedArtistsMap["artists/" + artistSnapshot.id] = artist
                            break
                        }
                    }
                }
            }

        }

        return Resource.Success(mapOf("favArtistsMap" to favArtistsMap,
            "recommendedArtistsMap" to recommendedArtistsMap))
    }

    suspend fun getRecommendedEvents(favArtistsMap: Map<String,
            Artist>,recommendedArtistsMap: Map<String,Artist>): Resource<List<Event>>{
        val comingEventsList = mutableListOf<Event>()

        val now = Date.from(ZonedDateTime.now().toInstant())

        //getComingEvents
        val querySnapshotComingEvents = firebaseFirestore.collection("events")
            .whereGreaterThan("startDateTime",now)
            .orderBy("startDateTime",Query.Direction.ASCENDING)
            .get().await()


        for(document in querySnapshotComingEvents) {
            val locationMap = document.get("location") as Map<String, Any>
            val placeName = locationMap["placeName"].toString()
            val placeAddress = locationMap["placeAddress"].toString()
            val placeGeoPoint = locationMap["placeLatLng"] as GeoPoint

            val formatter = SimpleDateFormat(Constants.DATE_TIME_FORMAT)
            val startDateTime =  formatter.format(document.getTimestamp("startDateTime")!!.toDate())

            val event = Event(document.getString("header")!!,
                startDateTime,
                document.getString("shortDescription")!!,
                document.getString("ticketsLink")!!,
                placeName,placeAddress,placeGeoPoint.latitude,placeGeoPoint.longitude,
                (document.get("artist") as DocumentReference).path,
                document.id)

            val artistID =   (document.get("artist") as DocumentReference).path

            if(favArtistsMap.containsKey(artistID) || recommendedArtistsMap.containsKey(artistID)) {
                comingEventsList.add(event)
            }
        }
        return Resource.Success(comingEventsList)

    }
 }

