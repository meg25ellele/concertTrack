package com.example.concerttrack.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.concerttrack.R
import com.example.concerttrack.models.Event
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.artist_event_item.view.*

class ArtistEventsAdapter(private val artistEventsList: List<Event>) : RecyclerView.Adapter<ArtistEventsAdapter.ArtistEventViewHolder>() {

    inner class ArtistEventViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistEventViewHolder {
        return ArtistEventViewHolder((
                LayoutInflater.from(parent.context).inflate(
                    R.layout.artist_event_item,
                    parent,
                    false
                )
                ))
    }

    override fun getItemCount(): Int {
        return artistEventsList.size
    }

    override fun onBindViewHolder(holder: ArtistEventViewHolder, position: Int) {
        val artistEvent = artistEventsList[position]
        holder.itemView.apply {
            eventName.text = artistEvent.header
            whenInput.text = artistEvent.startDate + " " + artistEvent.startTime
            whereInput.text = artistEvent.placeName + ", " + artistEvent.placeAddress


            if(position+1 == artistEventsList.size) {
                seperateLine.visibility = View.GONE
            }
        }
    }
}