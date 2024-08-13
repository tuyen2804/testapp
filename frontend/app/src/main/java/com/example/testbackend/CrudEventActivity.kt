package com.example.testbackend

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.appcompat.app.AppCompatActivity
import com.example.testbackend.databinding.ActivityCrudEventBinding
import com.example.testbackend.network.RetrofitInstance
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CrudEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCrudEventBinding
    private var imageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            binding.bgEvent.setImageURI(it)
            Log.d(TAG, "Image URI selected: $it")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrudEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Button to pick image from gallery
        binding.addBgEvent.setOnClickListener {
            Log.d(TAG, "Image picker launched")
            pickImageLauncher.launch("image/*")
        }

        // Button to upload image
        binding.addEvent.setOnClickListener {
            Log.d(TAG, "Upload button clicked")
            uploadImage()
        }
    }

    private fun uploadImage() {
        if (imageUri == null) {
            Log.d(TAG, "No image selected")
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val imageFile = File.createTempFile("temp_image", ".jpg", cacheDir)
        try {
            // Copy the content from the selected image URI to the temp file
            Log.d(TAG, "Creating temp file at: ${imageFile.absolutePath}")
            contentResolver.openInputStream(imageUri!!)?.use { inputStream ->
                imageFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                    Log.d(TAG, "Image data copied to temp file")
                }
            }

            // Prepare the request body and multipart part
            val mimeType = contentResolver.getType(imageUri!!) ?: "image/jpeg"
            val requestBody = imageFile.asRequestBody(mimeType.toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", imageFile.name, requestBody)

            // Log multipart part details
            Log.d(TAG, "File part created: ${filePart.headers}")

            // Perform the upload operation
            RetrofitInstance.api.uploadImage(filePart).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val jsonResponse = response.body()
                        val imageUrl = jsonResponse?.get("path")?.asString
                        if (imageUrl != null) {
                            Log.d(TAG, "Image uploaded successfully: $imageUrl")
                            Toast.makeText(this@CrudEventActivity, "Image uploaded successfully: $imageUrl", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.d(TAG, "Failed to retrieve image URL from response")
                            showError("Failed to upload image")
                        }
                    } else {
                        Log.d(TAG, "Failed to upload image. Code: ${response.code()}, Message: ${response.message()}")
                        showError("Failed to upload image")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.d(TAG, "Upload failed: ${t.message}", t)
                    showError("Failed to upload image: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.d(TAG, "Error creating temp file: ${e.message}", e)
            showError("Error uploading image")
        }
    }

    private fun showError(message: String) {
        Log.d(TAG, "Error: $message")
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "CrudEventActivity"
    }
}
