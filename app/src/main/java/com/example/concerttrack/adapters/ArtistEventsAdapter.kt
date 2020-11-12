package com.example.concerttrack.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.concerttrack.R
import com.example.concerttrack.models.Artist
import com.example.concerttrack.models.Event
import com.example.concerttrack.util.Constants.Companion.DATE_FORMAT
import com.example.concerttrack.util.Constants.Companion.DATE_TIME_FORMAT
import com.example.concerttrack.util.Constants.Companion.DATE_TIME_FORMATTER
import com.example.concerttrack.util.Constants.Companion.TIME_FORMAT
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.artist_event_item.view.*
import kotlinx.android.synthetic.main.artist_event_item.view.eventName
import kotlinx.android.synthetic.main.artist_event_item.view.seperateLine
import kotlinx.android.synthetic.main.artist_event_item.view.whenInput
import kotlinx.android.synthetic.main.artist_event_item.view.whereInput
import kotlinx.android.synthetic.main.event_item.view.*
import java.text.SimpleDateFormat
import java.util.logging.SimpleFormatter

class ArtistEventsAdapter(private val artistEventsList: List<Event>, private val artist: Artist,
                          private val imgPath: String?) : RecyclerView.Adapter<ArtistEventsAdapter.ArtistEventViewHolder>() {

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
            whenInput.text = artistEvent.startDateTime
            whereInput.text = artistEvent.placeName + ", " + artistEvent.placeAddress
            if(position+1 == artistEventsList.size) {
                seperateLine.setBackgroundColor(resources.getColor(R.color.white))
            }
            setOnClickListener {
                onItemClickListener?.let {it(artistEvent,artist,imgPath)}
            }
        }
    }

    private var onItemClickListener: ((Event,Artist?, String?) -> Unit)? = null

    fun setOnItemClickListener(listener: (Event,Artist?,String?) -> Unit) {
        onItemClickListener = listener
    }
}