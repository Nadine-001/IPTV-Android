package com.example.tvapp

import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.tvapp.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class RoomServiceDescription : AppCompatActivity() {
    private val TAG: String = "RoomDescription"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_service_description)

        supportActionBar?.hide()
        Log.d(TAG, "RoomServiceDescription activity created")
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
        val dateFormat = DateFormat.getDateInstance().format(calendar.time)

        val dateTextView = findViewById<TextView>(R.id.text_date)
        dateTextView.text = dateFormat

        val dayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
        val dayTextView = findViewById<TextView>(R.id.text_day)
        val dayOfWeek = dayFormat.format(calendar.time)
        dayTextView.text = dayOfWeek

        val serviceButton = findViewById<Button>(R.id.serviceButton)
        serviceButton.setOnClickListener {
            val intent = Intent(this, RoomServiceDetail::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        getRoomHeader()
        getDataFromApi()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_UP,
                KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    val serviceButton = findViewById<Button>(R.id.serviceButton)
                    serviceButton.requestFocus()
                    serviceButton.scaleX = 1.2f
                    serviceButton.scaleY = 1.2f
                    return true
                }
                KeyEvent.KEYCODE_ENTER -> {
                    if (currentFocus == findViewById<Button>(R.id.serviceButton)) {
                        navigateToServiceDetail()
                        return true
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }
    private fun navigateToServiceDetail() {
        val intent = Intent(this, RoomServiceDetail::class.java)
        startActivity(intent)
    }

    private fun getMacAddress(): String? {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        val wifiInfo = wifiManager?.connectionInfo
        return wifiInfo?.macAddress
    }

    private fun getRoomHeader(){
        val macAddress = getMacAddress()
        if (macAddress != null) {
            ApiService.endpoint.getHeader(macAddress)
                .enqueue(object : Callback<RoomHeaderModel> {
                    override fun onResponse(
                        call: Call<RoomHeaderModel>,
                        response: Response<RoomHeaderModel>
                    ) {
                        val result = response.body()
                        if (result != null) {
                            val guestName = findViewById<TextView>(R.id.guest_name)
                            guestName.text = result.guest_name
                        }
                        printLog(result.toString())
                    }

                    override fun onFailure(call: Call<RoomHeaderModel>, t: Throwable) {
                        printLog("API Call Failed: ${t.message}")
                    }

                })
        }

    }

    private fun getDataFromApi() {
        val macAddress = getMacAddress()

        if (macAddress != null) {
        Log.d(TAG, "getDataFromApi called")
        ApiService.endpoint.getDesc(macAddress)
            .enqueue(object : Callback<DescriptionModel> {
                override fun onResponse(call: Call<DescriptionModel>, response: Response<DescriptionModel>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null) {
                            // Display guest_name in TextView
//                            val textName = findViewById<TextView>(R.id.text_name)
//                            textName.text = result.guest_name

                            // Display guest_name in TextView
                            val textRoomType = findViewById<TextView>(R.id.helloTextView)
                            textRoomType.text = result.room_type

                            // Display room_number in TextView
                            val textRoomNumber = findViewById<TextView>(R.id.room_number)
                            textRoomNumber.text = result.room_number.toString()

//                            val roomDescription = findViewById<TextView>(R.id.welcomeTextView)
//                            roomDescription.text = result.room_facility

                            val roomFacility = findViewById<TextView>(R.id.welcomeTextView)
                            roomFacility.text = result.room_facility
                            // Display hotel_greeting in TextView
                            val roomDescription = findViewById<TextView>(R.id.welcomeTextView3)
                            val htmlContent = result.room_description
                            val spannedHtmlContent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY)
                            } else {
                                @Suppress("DEPRECATION")
                                Html.fromHtml(htmlContent)
                            }
                            roomDescription.text = spannedHtmlContent


                            val roomImg = findViewById<ImageView>(R.id.wifi)

                            roomImg?.let {
                                Glide.with(this@RoomServiceDescription)
                                    .load(result.room_image)
                                    .into(it)
                            }

                            printLog("API Response: $result")
                        }
                        printLog(result.toString())
                    }
                }

                                override fun onFailure(call: Call<DescriptionModel>, t: Throwable) {
                    printLog("API Call Failed: ${t.message}")
                }

//                override fun onResponse(
//                    call: Call<DescriptionModel>,
//                    response: Response<DescriptionModel>
//                ) {
//                    Log.d(TAG, "onResponse called")
//                    if (response.isSuccessful) {
//                        val result = response.body()
//                        if (result != null) {
//                            Log.d(TAG, "API Response: $result")
//                            // Additional logs for specific data, e.g., room_type, room_number, room_facility
//                            Log.d(TAG, "room_type: ${result.room_type}")
//                            Log.d(TAG, "room_number: ${result.room_number}")
//                            Log.d(TAG, "room_facility: ${result.room_facility}")
//                        }
//                    } else {
//                        Log.e(TAG, "API Call not successful: ${response.code()}")
//                    }
//                    Log.d(TAG, "onResponse completed")
//                }
//
//
//                override fun onFailure(call: Call<DescriptionModel>, t: Throwable) {
//                    Log.e(TAG, "API Call Failed: ${t.message}")
//                    val errorResponse = (t as? HttpException)?.response()?.errorBody()?.string()
//                    Log.e(TAG, "Error Response: $errorResponse")
//                }


            })}

    }

    private fun printLog(message: String) {
        Log.d(TAG, message)
    }


}