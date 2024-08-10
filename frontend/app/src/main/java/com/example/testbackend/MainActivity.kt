package com.example.testbackend

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testbackend.model.AuthResponse
import com.example.testbackend.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val logtex = findViewById<TextView>(R.id.log)
        loginButton.setOnClickListener {
            if (isNetworkAvailable()) {
                val username = usernameEditText.text.toString()
                val password = passwordEditText.text.toString()

                val user = User(username, password)
                RetrofitInstance.api.login(user).enqueue(object : Callback<AuthResponse> {
                    override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                        if (response.isSuccessful) {
                            val authResponse = response.body()
                            if (authResponse?.auth == true) {
                                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                                intent.putExtra("username", username)
                                startActivity(intent)
                            } else {
                                logtex.text = "Sai thông tin"
                                Log.d("LoginActivity", "Auth failed: ${authResponse?.auth}")
                            }
                        } else {
                            logtex.text = "Lỗi: ${response.message()}"
                            Log.d("LoginActivity", "Response error: ${response.message()}")
                        }
                    }


                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
