package com.example.documentmedia

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.documentmedia.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    companion object{
        var isPlaying: Boolean = false
        var musicService: MyService? = null

    }

    lateinit var binding: ActivityMainBinding
    private val apiService = RetrofitService.getInstance()

    private val serviceIntent by lazy {
        Intent(this, MyService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener {
            // Start the service when the "Start" button is clicked
            startService()
        }

        binding.btnStop.setOnClickListener {
            // Stop the service when the "Stop" button is clicked
            stopService()
        }

    }

    private fun startService() {
        // Check if the service is already running
        if (!isServiceRunning(MyService::class.java)) {
            // Start the service with the audio URL
            serviceIntent.putExtra("audioUrl", "https://aydym.com/audioFiles/original/2023/07/11/07/34/b1b1282a-a4c5-485b-bd2c-a4d956d08a48.mp3")
            startService(serviceIntent)
        }
        var isPlaying: Boolean = true

    }

    private fun stopService() {
        // Stop the service
        stopService(serviceIntent)
        var isPlaying: Boolean = false

    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun getNewSongs() {
        val apiService =
            apiService.getNewSongs(
            )
        var url: String? = null
        apiService?.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val jsonresponse: String = response.body().toString()
                        // on below line we are initializing our adapter.
                        Log.d("response", jsonresponse.toString())
                    } else {
                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        )
                    }
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.e(
                    "ERROR",
                    t.message.toString()
                )
            }
        })

    }
}