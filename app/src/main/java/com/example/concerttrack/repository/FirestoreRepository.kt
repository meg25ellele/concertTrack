package com.example.concerttrack.repository

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
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



//    fun retrieveMusicGenres() {
//        CoroutineScope(Dispatchers.IO).launch {
//            try{
//                val querySnapshot = musicGenresCollectionRef.get().await()
//
//                for(document in querySnapshot.documents) {
//                    val musicGenre = document.toObject<MusicGenre>()
//                    if (musicGenre != null) {
//                        musicGenresList.add(musicGenre)
//                    }
//                }
//
//                withContext(Dispatchers.Main) {
//                    musicGenresListLiveData?.postValue(musicGenresList)
//                }
//
//            }catch(e:Exception){
//                Toast.makeText(application,e.message,Toast.LENGTH_LONG).show()
//            }
//        }
//    }

}