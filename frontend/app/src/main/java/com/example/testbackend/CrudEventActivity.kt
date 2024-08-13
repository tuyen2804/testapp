package com.example.testbackend

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.appcompat.app.AppCompatActivity
import com.example.testbackend.databinding.ActivityCrudEventBinding
import com.example.testbackend.network.RetrofitInstance
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

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


        binding.addBgEvent.setOnClickListener {
            Log.d(TAG, "Image picker launched")
            pickImageLauncher.launch("image/*")
        }

        binding.addEvent.setOnClickListener {
            Log.d(TAG, "Upload button clicked")
            uploadImage()
        }
    }

    private fun uploadImage() {
        if (imageUri == null) {
            Log.d(TAG, "No image selected")
            Toast.makeText(this, "Vui lòng chọn một ảnh", Toast.LENGTH_SHORT).show()
            return
        }

        try {

            val inputStream = contentResolver.openInputStream(imageUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val byteArray = outputStream.toByteArray()


            val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

            val jsonObject = JsonObject().apply {
                addProperty("image", base64Image)
            }

            // Perform the upload operation
            RetrofitInstance.api.uploadBase64Image(jsonObject).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val imageUrl = response.body()?.get("path")?.asString
                        Log.d(TAG, "Image uploaded successfully: $imageUrl")
                        Toast.makeText(this@CrudEventActivity, "Ảnh đã tải lên thành công: $imageUrl", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d(TAG, "Failed to upload image. Code: ${response.code()}, Message: ${response.message()}")
                        showError("Tải ảnh thất bại")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.d(TAG, "Upload failed: ${t.message}", t)
                    showError("Tải ảnh thất bại: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.d(TAG, "Error converting image: ${e.message}", e)
            showError("Lỗi tải ảnh")
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
