package com.example.concerttrack.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.concerttrack.R
import com.example.concerttrack.models.Artist
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fav_artist_item.view.*

class FavArtistsAdapter(private val artistsList: List<Artist>, private  val imagesMap: Map<String, Uri>):
                                                RecyclerView.Adapter<FavArtistsAdapter.FavArtistViewHolder>(){

    inner class FavArtistViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavArtistViewHolder {
        return FavArtistViewHolder((
                LayoutInflater.from(parent.context).inflate(
                    R.layout.fav_artist_item,
                    parent,
                    false)
                ))
    }

    override fun getItemCount(): Int {
        return artistsList.size
    }

    override fun onBindViewHolder(holder: FavArtistViewHolder, position: Int) {
        val artist = artistsList[position]
        holder.itemView.apply {
            artistName.text = artist.name
            likeBtn.setImageDrawable(resources.getDrawable(R.drawable.heart_red))


            val artistPng = artist.id + ".png"
            var imgPath: String? = null
            if(imagesMap.get(artistPng) != null) {
                val image = imagesMap.get(artistPng)!!
                imgPath = image.toString()
                Picasso.get().load(image).into(artistImage)
            }

            setOnClickListener {
                onItemClickListener?.let { it(artist,imgPath) }
            }

            artistImage.setOnClickListener {
                onItemClickListener?.let { it(artist,imgPath) }
            }

            likeBtn.setOnClickListener {
                likeBtn.setImageDrawable(resources.getDrawable(R.drawable.heart_empty))
                onHeartClickListener?.let { it(artist,position)  }
            }



        }
    }



    private var onItemClickListener: ((Artist,String?) -> Unit)? = null
    private var onHeartClickListener: ((Artist,Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Artist,String?) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnHeartClickListener (listener: (Artist,Int) -> Unit) {
        onHeartClickListener = listener
    }



}