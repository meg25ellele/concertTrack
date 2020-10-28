package com.example.concerttrack.repository

import android.app.Application
import android.util.Log
import com.example.concerttrack.models.Artist

import com.example.concerttrack.models.MusicGenre
import com.example.concerttrack.models.User
import com.example.concerttrack.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class CloudFirestoreRepository(private val application: Application) {

    private var firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun retrieveMusicGenres(): Resource<MutableList<MusicGenre>> {
        val musicGenresList = mutableListOf<MusicGenre>()
        val  querySnapshot = firebaseFirestore.collection("musicGenres").get().await()

        for(document in querySnapshot) {
            val genreName = document.getString("name")
            musicGenresList.add(MusicGenre(genreName!!))
        }
        return Resource.Success(musicGenresList)
    }

    suspend fun addNewUser(id: String, email: String, name: String): Resource<Boolean> {
        val user = User(id,email,name)
        firebaseFirestore.collection("users").document(id).set(user).await()

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

    suspend fun addNewArtist(id:String, email:String, name:String,description:String?,
                             facebookLink:String?, youtubeLink:String?,spotifyLink:String?,myGenres:List<String>?)
                                : Resource<Boolean>{

        val artist =
            Artist(id, email, name, description, facebookLink, youtubeLink, spotifyLink,myGenres)
        firebaseFirestore.collection("artists").document(id).set(artist).await()

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


 }

