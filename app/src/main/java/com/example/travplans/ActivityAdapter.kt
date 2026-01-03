package com.example.travplans

import android.app.TimePickerDialog
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class ActivityAdapter(
    private val activities: MutableList<ActivityItem>,
    private val onTakePhotoClick: (Int) -> Unit,
    private val onUploadPhotoClick: (Int) -> Unit
) : RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val descriptionEditText: EditText = view.findViewById(R.id.descriptionEditText)
        val startTimeEditText: EditText = view.findViewById(R.id.startTimeEditText)
        val endTimeEditText: EditText = view.findViewById(R.id.endTimeEditText)
        val removeActivityButton: Button = view.findViewById(R.id.removeActivityButton)
        val takePhotoButton: Button = view.findViewById(R.id.takePhotoButton)
        val uploadPhotoButton: Button = view.findViewById(R.id.uploadPhotoButton)
        val photoPreviewImageView: ImageView = view.findViewById(R.id.photoPreviewImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_edit_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = activities[position]

        // Clear existing listeners to prevent issues with recycled views
        (holder.itemView.getTag(R.id.descriptionEditText) as? TextWatcher)?.let {
            holder.descriptionEditText.removeTextChangedListener(it)
        }

        holder.descriptionEditText.setText(activity.description)
        holder.startTimeEditText.setText(activity.startTime)
        holder.endTimeEditText.setText(activity.endTime)

        if (activity.imageUri != null) {
            holder.photoPreviewImageView.setImageURI(Uri.parse(activity.imageUri))
            holder.photoPreviewImageView.visibility = View.VISIBLE
        } else {
            holder.photoPreviewImageView.visibility = View.GONE
        }

        val descriptionWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    activities[holder.adapterPosition].description = s.toString()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        holder.descriptionEditText.addTextChangedListener(descriptionWatcher)
        holder.itemView.setTag(R.id.descriptionEditText, descriptionWatcher)

        holder.startTimeEditText.isFocusable = false
        holder.startTimeEditText.isClickable = true
        holder.startTimeEditText.setOnClickListener { showTimePickerDialog(holder, true) }

        holder.endTimeEditText.isFocusable = false
        holder.endTimeEditText.isClickable = true
        holder.endTimeEditText.setOnClickListener { showTimePickerDialog(holder, false) }

        holder.takePhotoButton.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                onTakePhotoClick(holder.adapterPosition)
            }
        }

        holder.uploadPhotoButton.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                onUploadPhotoClick(holder.adapterPosition)
            }
        }

        holder.removeActivityButton.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                activities.removeAt(holder.adapterPosition)
                notifyDataSetChanged()
            }
        }
    }

    private fun showTimePickerDialog(holder: ViewHolder, isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            holder.itemView.context,
            { _, hourOfDay, minute ->
                val time = String.format("%02d:%02d", hourOfDay, minute)
                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    if (isStartTime) {
                        holder.startTimeEditText.setText(time)
                        activities[holder.adapterPosition].startTime = time
                    } else {
                        holder.endTimeEditText.setText(time)
                        activities[holder.adapterPosition].endTime = time
                    }
                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24-hour format
        )
        timePickerDialog.show()
    }

    override fun getItemCount() = activities.size
}