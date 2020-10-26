package com.example.concerttrack.repository

import android.app.Application

import com.example.concerttrack.models.MusicGenre
import com.example.concerttrack.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class FirestoreRepository(private val application: Application) {

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

}