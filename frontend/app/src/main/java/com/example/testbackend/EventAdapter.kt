package com.example.testbackend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testbackend.model.Event

class EventAdapter(private val events: List<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.eventName)
        val eventTime: TextView = itemView.findViewById(R.id.eventTime)
        val eventLocation: TextView = itemView.findViewById(R.id.eventLocation)
        val bgEvent: ImageView = itemView.findViewById(R.id.bg_Event)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_event_home, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.eventName.text = event.name
        holder.eventTime.text = "${event.start_time} - ${event.end_time}"
        holder.eventLocation.text = event.location

        // Sử dụng Glide để tải ảnh nền từ URL
        if (event.background_image_url != null) {
            Glide.with(holder.itemView.context)
                .load("http://192.168.0.4:3000"+event.background_image_url)
                .into(holder.bgEvent)
        } else {
            holder.bgEvent.setImageResource(R.drawable.message_background) // Hình ảnh mặc định nếu không có URL
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }
}
