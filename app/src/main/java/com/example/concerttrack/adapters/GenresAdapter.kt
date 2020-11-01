package com.example.concerttrack.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.concerttrack.R
import com.example.concerttrack.models.MusicGenre
import kotlinx.android.synthetic.main.music_genre_item.view.*

class GenresAdapter( private val musicGenresList: List<MusicGenre>) : RecyclerView.Adapter<GenresAdapter.GenreViewHolder>() {


    inner class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        return GenreViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.music_genre_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return musicGenresList.size
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val musicGenre = musicGenresList[position]
        holder.itemView.apply {
            genreName.text = musicGenre.name
            if(musicGenre.chosen) {
                genreName.setBackgroundColor(resources.getColor(R.color.toggleColor))
            }

            genreName.setOnClickListener {

                    if(musicGenre.chosen) {
                        genreName.setBackgroundResource(R.drawable.edit_text_background)
                        musicGenre.chosen = false
                    } else {
                        genreName.setBackgroundColor(resources.getColor(R.color.toggleColor))
                        musicGenre.chosen = true
                    }
            }
        }
    }

}