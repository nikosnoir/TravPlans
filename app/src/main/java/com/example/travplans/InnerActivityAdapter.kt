package com.example.travplans

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class InnerActivityAdapter(private val activities: List<ActivityItem>) : RecyclerView.Adapter<InnerActivityAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeTextView: TextView = view.findViewById(R.id.timeTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        val photoImageView: ImageView = view.findViewById(R.id.photoImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = activities[position]
        holder.timeTextView.text = "${activity.startTime} - ${activity.endTime}"
        holder.descriptionTextView.text = activity.description

        if (activity.imageUri != null) {
            holder.photoImageView.setImageURI(Uri.parse(activity.imageUri))
            holder.photoImageView.visibility = View.VISIBLE
        } else {
            holder.photoImageView.visibility = View.GONE
        }
    }

    override fun getItemCount() = activities.size
}