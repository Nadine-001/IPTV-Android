package com.example.tvapp

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.text.Html
import android.text.SpannableString
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.TextView
import com.example.tvapp.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class SplashScreen : AppCompatActivity() {

    private val TAG: String = "SplashScreen"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

//        setTitle("Date & Time");

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
        val dateFormat = DateFormat.getDateInstance().format(calendar.time)

        val dateTextView = findViewById<TextView>(R.id.text_date)
        dateTextView.text = dateFormat

        val dayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
        val dayTextView = findViewById<TextView>(R.id.text_day)
        val dayOfWeek = dayFormat.format(calendar.time)
        dayTextView.text = dayOfWeek

        val homeButton = findViewById<Button>(R.id.welcomeButton)
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }
    override fun onStart() {
        super.onStart()
        getDataFromApi()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_UP,
                KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    val welcomeButton = findViewById<Button>(R.id.welcomeButton)
                    welcomeButton.requestFocus()
                    welcomeButton.scaleX = 1.2f
                    welcomeButton.scaleY = 1.2f
                    return true
                }
                KeyEvent.KEYCODE_ENTER -> {
                    if (currentFocus == findViewById<Button>(R.id.welcomeButton)) {
                        navigateToMainActivity()
                        return true
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }


    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun getMacAddress(): String? {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        val wifiInfo = wifiManager?.connectionInfo
        return wifiInfo?.macAddress
    }



        private fun getDataFromApi() {
            val macAddress = getMacAddress()

            if (macAddress != null) {
                printLog("MAC Address: $macAddress")
                ApiService.endpoint.getGreeting(macAddress)
                    .enqueue(object : Callback<GreetingModel> {
                        override fun onResponse(
                            call: Call<GreetingModel>,
                            response: Response<GreetingModel>
                        ) {
                            if (response.isSuccessful) {
                                val result = response.body()
                                if (result != null) {
                                    // Display guest_name in TextView
                                    val textName = findViewById<TextView>(R.id.text_name)
                                    textName.text = result.guest_name

                                    // Display guest_name in TextView
                                    val textRoomType = findViewById<TextView>(R.id.room_type)
                                    textRoomType.text = result.room_type

                                    // Display room_number in TextView
                                    val textRoomNumber = findViewById<TextView>(R.id.room_number)
                                    textRoomNumber.text = result.room_number.toString()


                                    // Display hotel_greeting in TextView
                                    val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView)
                                    val htmlContent = result?.hotel_greeting // Menggunakan operator safe call (?) untuk memastikan result tidak null
                                    val spannedHtmlContent = if (htmlContent != null && !htmlContent.isEmpty()) { // Memeriksa apakah htmlContent tidak null dan tidak kosong
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                            Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY)
                                        } else {
                                            @Suppress("DEPRECATION")
                                            Html.fromHtml(htmlContent)
                                        }
                                    } else {
                                        SpannableString("") // Atau tindakan lain yang sesuai dengan kebutuhan Anda jika htmlContent null atau kosong
                                    }
                                    welcomeTextView.text = spannedHtmlContent


                                    printLog("API Response: $result")
                                } else {
                                    printLog("Response body is null.")
                                }
                                printLog(result.toString())
                            }
                        }

                        override fun onFailure(call: Call<GreetingModel>, t: Throwable) {
                            printLog((t.toString()))
                        }
                    })
            } else {
                // Handle the case where MAC address couldn't be obtained
                printLog("Unable to obtain MAC address.")
            }
        }


        private fun printLog(message: String) {
        Log.d(TAG, message)
    }
}