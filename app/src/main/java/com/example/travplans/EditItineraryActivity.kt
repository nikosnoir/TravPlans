package com.example.travplans

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travplans.databinding.ActivityEditItineraryBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditItineraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditItineraryBinding
    private lateinit var itineraryDay: ItineraryDay
    private lateinit var activityAdapter: ActivityAdapter
    private var currentPhotoPath: String? = null
    private var photoPosition: Int = -1

    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            dispatchTakePictureIntent()
        } else {
            Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestStoragePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selectImageLauncher.launch(intent)
        } else {
            Toast.makeText(this, "Storage permission is required to upload photos", Toast.LENGTH_SHORT).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            if (photoPosition != -1 && currentPhotoPath != null) {
                itineraryDay.activities[photoPosition].imageUri = currentPhotoPath
                activityAdapter.notifyItemChanged(photoPosition)
            }
        }
    }

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val uri = it.data?.data
            if (photoPosition != -1 && uri != null) {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                itineraryDay.activities[photoPosition].imageUri = uri.toString()
                activityAdapter.notifyItemChanged(photoPosition)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditItineraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itineraryDayFromIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_ITINERARY_DAY, ItineraryDay::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_ITINERARY_DAY) as? ItineraryDay
        }

        if (itineraryDayFromIntent == null) {
            finish()
            return
        }
        itineraryDay = itineraryDayFromIntent

        binding.dayTitleEditText.setText(itineraryDay.day)
        if (itineraryDay.date.isNotEmpty()) {
            binding.datePickerButton.text = itineraryDay.date
        }

        activityAdapter = ActivityAdapter(itineraryDay.activities, {
            photoPosition = it
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }, {
            photoPosition = it
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                selectImageLauncher.launch(intent)
            } else {
                requestStoragePermissionLauncher.launch(permission)
            }
        })
        binding.activitiesRecyclerView.adapter = activityAdapter
        binding.activitiesRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.datePickerButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                    calendar.set(year, month, dayOfMonth)
                    itineraryDay.date = sdf.format(calendar.time)
                    binding.datePickerButton.text = itineraryDay.date
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        binding.addActivityButton.setOnClickListener {
            itineraryDay.activities.add(ActivityItem("", "", ""))
            activityAdapter.notifyItemInserted(itineraryDay.activities.size - 1)
        }

        binding.saveButton.setOnClickListener {
            val updatedDayTitle = binding.dayTitleEditText.text.toString()
            val resultIntent = Intent()
            resultIntent.putExtra(EXTRA_ITINERARY_DAY, itineraryDay.copy(day = updatedDayTitle))
            resultIntent.putExtra(EXTRA_ITINERARY_POSITION, intent.getIntExtra(EXTRA_ITINERARY_POSITION, -1))
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also { 
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "${applicationContext.packageName}.provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePictureLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    companion object {
        const val EXTRA_ITINERARY_DAY = "com.example.travplans.EXTRA_ITINERARY_DAY"
        const val EXTRA_ITINERARY_POSITION = "com.example.travplans.EXTRA_ITINERARY_POSITION"
    }
}