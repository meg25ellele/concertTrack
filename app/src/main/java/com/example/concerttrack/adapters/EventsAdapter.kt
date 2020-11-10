package com.example.concerttrack.adapters

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.concerttrack.R
import com.example.concerttrack.models.Artist
import com.example.concerttrack.models.Event
import com.example.concerttrack.ui.ArtistPanelFragment
import com.example.concerttrack.util.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.event_item.view.*
import kotlinx.android.synthetic.main.event_item.view.eventName
import kotlinx.android.synthetic.main.event_item.view.seperateLine
import kotlinx.android.synthetic.main.event_item.view.whenInput
import kotlinx.android.synthetic.main.event_item.view.whereInput
import java.text.SimpleDateFormat

class EventsAdapter(private val eventsList: List<Event>, private  val artistsMap: Map<String,Artist>,
                    private  val imagesMap: Map<String,Uri>): RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder((
                LayoutInflater.from(parent.context).inflate(
                    R.layout.event_item,
                    parent,
                    false)
                ))
    }

    override fun getItemCount(): Int {
        return eventsList.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventsList[position]
        holder.itemView.apply {
            eventName.text = event.header
            whenInput.text = event.startDateTime
            whereInput.text = event.placeName + ", " + event.placeAddress
            artistInput.text = artistsMap.get(event.artistReferencePath)!!.name


            val artistPng = artistsMap.get(event.artistReferencePath)!!.id + ".png"
            var imgPath: String? = null
            Log.i("img",artistPng)
            if(imagesMap.get(artistPng) != null) {
                val image = imagesMap.get(artistPng)!!
                imgPath = image.toString()
                Picasso.get().load(image).into(eventIcon)
            }

            if(position+1 == eventsList.size) {
                seperateLine.setBackgroundColor(resources.getColor(R.color.white))
            }

            setOnClickListener {
                onItemClickListener?.let {it(event,artistsMap.get(event.artistReferencePath)!!,imgPath)}
            }
        }
    }

    private var onItemClickListener: ((Event,Artist,String?) -> Unit)? = null

    fun setOnItemClickListener(listener: (Event,Artist,String?) -> Unit) {
        onItemClickListener = listener
    }
}