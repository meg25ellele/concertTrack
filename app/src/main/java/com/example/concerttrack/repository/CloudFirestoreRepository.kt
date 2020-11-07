package com.example.concerttrack.repository

import android.accounts.AuthenticatorDescription
import android.app.Application
import android.util.Log
import com.example.concerttrack.models.Artist
import com.example.concerttrack.models.Event

import com.example.concerttrack.models.MusicGenre
import com.example.concerttrack.models.User
import com.example.concerttrack.util.Resource
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.lang.Exception


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


    suspend fun addNewEvent(newEvent: Event): Resource<Boolean>{
        val data = hashMapOf<String,Any>(
            "header" to newEvent.header,
            "startDateTime" to hashMapOf<String,Any>(
                "startDate" to newEvent.startDate,
                "startTime" to newEvent.startTime
            ),
            "shortDescription" to newEvent.shortDescription,
            "ticketsLink" to newEvent.ticketsLink,
            "location" to hashMapOf<String,Any>(
                "placeName" to newEvent.placeName,
                "placeAddress" to newEvent.placeAddress,
                "placeLatLng" to newEvent.placeLatLng
            ),
            "artist" to newEvent.artist
        )
        firebaseFirestore.collection("events").add(data).await()
        return Resource.Success(true)
    }

 }

