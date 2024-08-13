package com.example.testbackend


import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.testbackend.model.Event
import com.example.testbackend.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
    private lateinit var eventAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnUpdateEvent=findViewById<LinearLayout>(R.id.btnUpdateEvent)
        btnUpdateEvent.setOnClickListener (){
            val intent= Intent(this@HomeActivity, CrudEventActivity::class.java)
            startActivity(intent)
        }
        // Cấu hình RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.rcvEvent)
        eventAdapter = EventAdapter(emptyList())
        recyclerView.adapter = eventAdapter

        // Lấy dữ liệu sự kiện từ backend
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        if (token != null) {
            RetrofitInstance.api.getEvents("$token").enqueue(object : Callback<List<Event>> {
                override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                    if (response.isSuccessful) {
                        response.body()?.let { events ->
                            Log.d(TAG, "onResponse: $events")
                            eventAdapter = EventAdapter(events)
                            recyclerView.adapter = eventAdapter
                        }
                    } else if (response.code() == 401) { // 401 Unauthorized
                        Toast.makeText(this@HomeActivity, "Phiên làm việc đã hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@HomeActivity, "Lỗi khi lấy dữ liệu sự kiện.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                    Toast.makeText(this@HomeActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Token không tồn tại.", Toast.LENGTH_SHORT).show()
        }
    }
}