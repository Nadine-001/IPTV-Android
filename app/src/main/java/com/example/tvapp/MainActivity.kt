package com.example.tvapp

//import androidx.appcompat.app.AppCompatActivity
import ChannelTV
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.tvapp.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class MainActivity : FragmentActivity() {

    private val TAG: String = "MainActivity"

    private lateinit var channelButton: RelativeLayout
    private lateinit var orderButton: RelativeLayout
    private lateinit var roomButton: RelativeLayout
    private lateinit var musicButton: RelativeLayout
    private lateinit var infoButton: RelativeLayout
    private lateinit var youtubeButton: RelativeLayout
    private lateinit var netflixButton: RelativeLayout

    private var isFirstFocusSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
        val dateFormat = DateFormat.getDateInstance().format(calendar.time)

        val dateTextView = findViewById<TextView>(R.id.text_date)
        dateTextView.text = dateFormat

        val dayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
        val dayTextView = findViewById<TextView>(R.id.text_day)
        val dayOfWeek = dayFormat.format(calendar.time)
        dayTextView.text = dayOfWeek

        channelButton = findViewById(R.id.channelButton)
        channelButton.setOnClickListener {
            navigateToHotelInfoAbout()
        }

        orderButton = findViewById(R.id.orderButton)
        orderButton.setOnClickListener {
            navigateToOrderFood()
        }

        roomButton = findViewById(R.id.roomButton)
        roomButton.setOnClickListener {
            navigateToHotelService()
        }

        musicButton = findViewById(R.id.musicButton)
        musicButton.setOnClickListener {
            openSpotify()
        }

        infoButton = findViewById(R.id.infoButton)
        infoButton.setOnClickListener {
            val intent = Intent(this, HotelInfo::class.java)
            startActivity(intent)
        }

        youtubeButton = findViewById(R.id.youtubeButton)
        youtubeButton.setOnClickListener {
            openYoutube()
        }

        netflixButton = findViewById(R.id.netflixButton)
        netflixButton.setOnClickListener {
            openNetflix()
        }

        SocketHandler.setSocket()
        SocketHandler.establishConnection(this)
    }

    override fun onStart() {
        super.onStart()
        getDataFromApi()
        getWifi()
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketHandler.closeConnection()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (!isFirstFocusSet) {
                    channelButton.requestFocus()
                    channelButton.scaleX = 1.2f
                    channelButton.scaleY = 1.2f
                    orderButton.clearFocus()
                    orderButton.scaleX = 1.0f
                    orderButton.scaleY = 1.0f
                    isFirstFocusSet = true
                    return true
                }
            }

            KeyEvent.KEYCODE_DPAD_UP -> {
                channelButton.clearFocus()
                channelButton.scaleX = 1.0f
                channelButton.scaleY = 1.0f
                orderButton.clearFocus()
                orderButton.scaleX = 1.0f
                orderButton.scaleY = 1.0f
                roomButton.clearFocus()
                roomButton.scaleX = 1.0f
                roomButton.scaleY = 1.0f
                musicButton.clearFocus()
                musicButton.scaleX = 1.0f
                musicButton.scaleY = 1.0f
                infoButton.clearFocus()
                infoButton.scaleX = 1.0f
                infoButton.scaleY = 1.0f
                youtubeButton.clearFocus()
                youtubeButton.scaleX = 1.0f
                youtubeButton.scaleY = 1.0f
                netflixButton.clearFocus()
                netflixButton.scaleX = 1.0f
                netflixButton.scaleY = 1.0f
                isFirstFocusSet = false
                return true
            }

            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (isFirstFocusSet && channelButton.isFocused) {
                    orderButton.requestFocus()
                    orderButton.scaleX = 1.2f
                    orderButton.scaleY = 1.2f
                    channelButton.clearFocus()
                    channelButton.scaleX = 1.0f
                    channelButton.scaleY = 1.0f
                    return true
                } else if (orderButton.isFocused){
                    roomButton.requestFocus()
                    roomButton.scaleX = 1.2f
                    roomButton.scaleY = 1.2f
                    orderButton.clearFocus()
                    orderButton.scaleX = 1.0f
                    orderButton.scaleY = 1.0f
                    return true
                } else if (roomButton.isFocused){
                    musicButton.requestFocus()
                    musicButton.scaleX = 1.2f
                    musicButton.scaleY = 1.2f
                    roomButton.clearFocus()
                    roomButton.scaleX = 1.0f
                    roomButton.scaleY = 1.0f
                    return true
                } else if (musicButton.isFocused){
                    infoButton.requestFocus()
                    infoButton.scaleX = 1.2f
                    infoButton.scaleY = 1.2f
                    musicButton.clearFocus()
                    musicButton.scaleX = 1.0f
                    musicButton.scaleY = 1.0f
                    return true
                } else if (infoButton.isFocused){
                    youtubeButton.requestFocus()
                    youtubeButton.scaleX = 1.2f
                    youtubeButton.scaleY = 1.2f
                    infoButton.clearFocus()
                    infoButton.scaleX = 1.0f
                    infoButton.scaleY = 1.0f
                    return true
                } else if (youtubeButton.isFocused){
                    netflixButton.requestFocus()
                    netflixButton.scaleX = 1.2f
                    netflixButton.scaleY = 1.2f
                    youtubeButton.clearFocus()
                    youtubeButton.scaleX = 1.0f
                    youtubeButton.scaleY = 1.0f
                    return true
                } else if (netflixButton.isFocused){
                    channelButton.requestFocus()
                    channelButton.scaleX = 1.2f
                    channelButton.scaleY = 1.2f
                    netflixButton.clearFocus()
                    netflixButton.scaleX = 1.0f
                    netflixButton.scaleY = 1.0f
                    return true
                }
            }

            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (isFirstFocusSet && orderButton.isFocused) {
                    channelButton.requestFocus()
                    channelButton.scaleX = 1.2f
                    channelButton.scaleY = 1.2f
                    orderButton.clearFocus()
                    orderButton.scaleX = 1.0f
                    orderButton.scaleY = 1.0f
                    return true
                } else if (roomButton.isFocused){
                    orderButton.requestFocus()
                    orderButton.scaleX = 1.2f
                    orderButton.scaleY = 1.2f
                    roomButton.clearFocus()
                    roomButton.scaleX = 1.0f
                    roomButton.scaleY = 1.0f
                    return true
                } else if (musicButton.isFocused){
                    roomButton.requestFocus()
                    roomButton.scaleX = 1.2f
                    roomButton.scaleY = 1.2f
                    musicButton.clearFocus()
                    musicButton.scaleX = 1.0f
                    musicButton.scaleY = 1.0f
                    return true
                } else if (infoButton.isFocused){
                    musicButton.requestFocus()
                    musicButton.scaleX = 1.2f
                    musicButton.scaleY = 1.2f
                    infoButton.clearFocus()
                    infoButton.scaleX = 1.0f
                    infoButton.scaleY = 1.0f
                    return true
                } else if (youtubeButton.isFocused){
                    infoButton.requestFocus()
                    infoButton.scaleX = 1.2f
                    infoButton.scaleY = 1.2f
                    youtubeButton.clearFocus()
                    youtubeButton.scaleX = 1.0f
                    youtubeButton.scaleY = 1.0f
                    return true
                } else if (netflixButton.isFocused){
                    youtubeButton.requestFocus()
                    youtubeButton.scaleX = 1.2f
                    youtubeButton.scaleY = 1.2f
                    netflixButton.clearFocus()
                    netflixButton.scaleX = 1.0f
                    netflixButton.scaleY = 1.0f
                    return true
                } else if (channelButton.isFocused){
                    netflixButton.requestFocus()
                    netflixButton.scaleX = 1.2f
                    netflixButton.scaleY = 1.2f
                    channelButton.clearFocus()
                    channelButton.scaleX = 1.0f
                    channelButton.scaleY = 1.0f
                    return true
                }
            }
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> {
                if (channelButton.isFocused) {
                    navigateToHotelInfoAbout()
                    return true
                } else if (orderButton.isFocused) {
                    navigateToOrderFood()
                    return true
                } else if (roomButton.isFocused) {
                    navigateToHotelService()
                    return true
                } else if (musicButton.isFocused) {
                    openSpotify()
                    return true
                } else if (youtubeButton.isFocused) {
                    openYoutube()
                    return true
                } else if (netflixButton.isFocused) {
                    openNetflix()
                    return true
                }
            }

        }
        return super.onKeyDown(keyCode, event)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun navigateToHotelInfoAbout() {
        val intent = Intent(this, ListChannelTV::class.java)
        startActivity(intent)
    }

    private fun navigateToOrderFood() {
        val intent = Intent(this, OrderFoodNew::class.java)
        startActivity(intent)
    }

    private fun navigateToHotelService() {
        val intent = Intent(this, RoomServiceDescription::class.java)
        startActivity(intent)
    }

    private fun openSpotify() {
        val spotifyUri = Uri.parse("https://www.spotify.com")

        val intent = Intent(Intent.ACTION_VIEW, spotifyUri)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Spotify Not Found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openYoutube() {
        val youtubeUri = Uri.parse("https://www.youtube.com")

        val intent = Intent(Intent.ACTION_VIEW, youtubeUri)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Youtube Not Found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openNetflix() {
        val netflixUri = Uri.parse("https://www.netflix.com")

        val intent = Intent(Intent.ACTION_VIEW, netflixUri)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Netflix Not Found", Toast.LENGTH_SHORT).show()
        }
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

    private fun getWifi(){
        val macAddress = getMacAddress()
        if (macAddress != null) {
            ApiService.endpoint.getHome(macAddress)
                .enqueue(object : Callback<HomeModel> {
                    override fun onResponse(
                        call: Call<HomeModel>,
                        response: Response<HomeModel>
                    ) {
                        val result = response.body()
                        if (result != null) {
//                            val guestName = findViewById<TextView>(R.id.guest_name)
//                            guestName.text = result.guest_name

                            val roomImg = findViewById<ImageView>(R.id.wifi)

                            roomImg?.let {
                                Glide.with(this@MainActivity)
                                    .load(result.hotel_wifi)
                                    .into(it)
                            }
                        }
                        printLog(result.toString())
                    }

                    override fun onFailure(call: Call<HomeModel>, t: Throwable) {
                        printLog("API Call Failed: ${t.message}")
                    }

                })
        }
    }

    private fun printLog(message: String) {
        Log.d(TAG, message)
    }
}