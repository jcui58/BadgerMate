package com.cs407.badgermate.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cs407.badgermate.R
import com.cs407.badgermate.data.Event
import java.text.SimpleDateFormat
import java.util.Locale

class EventAdapter(
    private var events: List<Event>
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.event_title)
        val timeView: TextView = itemView.findViewById(R.id.event_time)
        val locationView: TextView = itemView.findViewById(R.id.event_location)
        val descriptionView: TextView = itemView.findViewById(R.id.event_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        val timeFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

        holder.titleView.text = event.title
        holder.timeView.text = "${timeFormat.format(event.startTime)} - ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(event.endTime)}"
        
        if (!event.location.isNullOrEmpty()) {
            holder.locationView.text = "📍 ${event.location}"
            holder.locationView.visibility = android.view.View.VISIBLE
        } else {
            holder.locationView.visibility = android.view.View.GONE
        }

        holder.descriptionView.text = event.description ?: ""
        holder.descriptionView.visibility = if (event.description.isNullOrEmpty()) android.view.View.GONE else android.view.View.VISIBLE
    }

    override fun getItemCount(): Int = events.size

    fun updateEvents(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }
}
