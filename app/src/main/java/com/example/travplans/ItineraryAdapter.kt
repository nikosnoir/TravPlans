package com.example.travplans

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ItineraryAdapter(private val itineraryDays: List<ItineraryDay>, private val onItemClick: (ItineraryDay, Int) -> Unit) : RecyclerView.Adapter<ItineraryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayTextView: TextView = view.findViewById(R.id.dayTextView)
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        val activitiesRecyclerView: RecyclerView = view.findViewById(R.id.activitiesRecyclerView)

        fun bind(itineraryDay: ItineraryDay, position: Int, onItemClick: (ItineraryDay, Int) -> Unit) {
            dayTextView.text = itineraryDay.day
            dateTextView.text = itineraryDay.date
            activitiesRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            activitiesRecyclerView.adapter = InnerActivityAdapter(itineraryDay.activities)
            itemView.setOnClickListener { onItemClick(itineraryDay, position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_itinerary_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itineraryDays[position], position, onItemClick)
        holder.itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_in_bottom)
    }

    override fun getItemCount() = itineraryDays.size
}