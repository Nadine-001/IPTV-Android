package com.example.tvapp

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.tvapp.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class RoomServiceDetail : AppCompatActivity() {

    private val TAG: String = "ServicePage"
    private var isFirstFocusSet: Boolean = false
    private var focusedPosition: Int = 0

    private var recyclerView: RecyclerView? = null
    private var recyclerViewServiceAdapter: RecyclerViewServiceAdapter? = null
    private var serviceList: ArrayList<ServicesData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_service_detail)
        supportActionBar?.hide()

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
        val dateFormat = DateFormat.getDateInstance().format(calendar.time)

        val dateTextView = findViewById<TextView>(R.id.text_date)
        dateTextView.text = dateFormat

        val dayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
        val dayTextView = findViewById<TextView>(R.id.text_day)
        val dayOfWeek = dayFormat.format(calendar.time)
        dayTextView.text = dayOfWeek

        serviceList = ArrayList()

        recyclerView = findViewById<View>(R.id.rvthings) as RecyclerView
        recyclerViewServiceAdapter = RecyclerViewServiceAdapter(ArrayList())
        val numberOfColumns = 1
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, numberOfColumns, LinearLayoutManager.HORIZONTAL, false)

        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = recyclerViewServiceAdapter

        getRoomHeader()
        getDataApi()

        val cartButton = findViewById<View>(R.id.cart)
        cartButton.setOnClickListener {
            val intent = Intent(this, RoomServiceReq::class.java)
            startActivity(intent)
        }

    }

    private fun addToCartClicked(serviceId: Int, serviceName: String) {
        printLog("Service ID for $serviceName: $serviceId")
        val macAddress = getMacAddress() ?: ""
        val addToCartRequest = AddToCartRequest(macAddress, serviceId)

        ApiService.endpoint.addToCartService(addToCartRequest)
            .enqueue(object : Callback<AddToCartRequest> {
                override fun onResponse(call: Call<AddToCartRequest>, response: Response<AddToCartRequest>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null) {
                            printLog("POST Response: ${response.message()}")
                            Toast.makeText(applicationContext, "Success add to cart: $serviceName", Toast.LENGTH_SHORT).show()
                        } else {
                            printLog("Response body is null.")
                            Toast.makeText(applicationContext, "Failed add to cart, try again", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        printLog("POST Request Failed: ${response.message()}, Code: ${response.code()}")
                        val errorBody = response.errorBody()?.string()
                        printLog("Error Body: $errorBody")
                    }
                }

                override fun onFailure(call: Call<AddToCartRequest>, t: Throwable) {
                    printLog("POST Request Failed: ${t.message}")
                    t.printStackTrace()
                }
            })
    }

    private fun logHoveredCardData(position: Int) {
        if (position >= 0 && position < serviceList.size) {
            val hoveredCardData = serviceList[position]
            printLog("Hovered Card Data: $hoveredCardData")
            handleHoveredCard(hoveredCardData)
        } else {
            printLog("Invalid position or serviceList is empty. Unable to retrieve Hovered Card Data for position: $position")
        }

    }


    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        Log.d(TAG, "Key Event: ${event?.keyCode}")
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    onKeyDown(KeyEvent.KEYCODE_DPAD_DOWN, event)
                    return true
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, event)
                    return true
                }
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, event)
                    return true
                }
                KeyEvent.KEYCODE_ENTER -> {
                    onKeyDown(KeyEvent.KEYCODE_ENTER, event)
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }
    private var isCartZoomed = false
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val recyclerViewServiceList = findViewById<RecyclerView>(R.id.rvthings)
        val layoutManager = recyclerViewServiceList.layoutManager as? LinearLayoutManager

        if (layoutManager == null) {
            printLog("LayoutManager is null")
            return super.onKeyDown(keyCode, event)
        }

        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                Log.d(TAG, "DPAD_DOWN pressed")
                if (!isFirstFocusSet) {
                    val firstVisibleItem = layoutManager.findViewByPosition(0)
                    firstVisibleItem?.setBackgroundResource(R.drawable.hover)
                    logHoveredCardData(focusedPosition)
                    isFirstFocusSet = true
                    return true
                } else {
                    val firstVisibleItem = layoutManager.findViewByPosition(focusedPosition)
                    firstVisibleItem?.setBackgroundResource(0)

                    val cartView = findViewById<View>(R.id.cart)
                    cartView.scaleX = 1.5f
                    cartView.scaleY = 1.5f
                    printLog("Cart view is highlighted")
                    isCartZoomed = true
                    return true
                }
            }

            KeyEvent.KEYCODE_DPAD_UP -> {
                if (isCartZoomed) {
                    val cartView = findViewById<View>(R.id.cart)
                    cartView.scaleX = 1f
                    cartView.scaleY = 1f

                    val firstVisibleItem = layoutManager.findViewByPosition(focusedPosition)
                    firstVisibleItem?.setBackgroundResource(R.drawable.hover)
                }
            }

            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                Log.d(TAG, "DPAD_RIGHT pressed")
                if (isFirstFocusSet) {
                    focusedPosition++
                    val itemCount = layoutManager.itemCount

                    if (focusedPosition < itemCount) {
                        val nextVisibleItem = layoutManager.findViewByPosition(focusedPosition)
                        val currentVisibleItem = layoutManager.findViewByPosition(focusedPosition - 1)

                        if (nextVisibleItem == null) {
                            layoutManager.scrollToPositionWithOffset(focusedPosition, (recyclerViewServiceList.width / 2))
                            recyclerViewServiceList.post {
                                val updatedVisibleItem = layoutManager.findViewByPosition(focusedPosition)
                                if (updatedVisibleItem != null) {
                                    logHoveredCardData(focusedPosition)
                                    updatedVisibleItem.setBackgroundResource(R.drawable.hover)
                                    currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)
                                } else {
                                    logHoveredCardData(focusedPosition)
                                    currentVisibleItem?.let {
                                        it.setBackgroundResource(R.drawable.hover)
                                        val distanceToCenter = (recyclerViewServiceList.width - it.width) / 2
                                        recyclerViewServiceList.smoothScrollBy((distanceToCenter ?: 0) / 10, 0)
                                    }
                                }
                            }
                        } else {
                            logHoveredCardData(focusedPosition)
                            nextVisibleItem.setBackgroundResource(R.drawable.hover)
                            currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)
                            val distanceToCenter = (recyclerViewServiceList.width - nextVisibleItem.width) / 2
                            recyclerViewServiceList.smoothScrollBy(distanceToCenter / 10, 0)
                        }
                    } else {
                        val lastItemPosition = focusedPosition - 1
                        val smoothScroller = object : LinearSmoothScroller(recyclerViewServiceList.context) {
                            override fun getVerticalSnapPreference(): Int {
                                return SNAP_TO_START
                            }
                        }
                        smoothScroller.targetPosition = lastItemPosition
                        layoutManager.startSmoothScroll(smoothScroller)
                    }
                    return true
                }
            }

            KeyEvent.KEYCODE_DPAD_LEFT -> {
                Log.d(TAG, "DPAD_LEFT pressed")
                if (isFirstFocusSet) {
                    if (focusedPosition >= 0) {
                        focusedPosition--
                        val previousVisibleItem = layoutManager.findViewByPosition(focusedPosition)
                        val currentVisibleItem = layoutManager.findViewByPosition(focusedPosition + 1)

                        if (previousVisibleItem == null) {
                            layoutManager.scrollToPositionWithOffset(focusedPosition, (recyclerViewServiceList.width / 2))
                            recyclerViewServiceList.post {
                                val updatedVisibleItem = layoutManager.findViewByPosition(focusedPosition)
                                if (updatedVisibleItem != null) {
                                    logHoveredCardData(focusedPosition)
                                    updatedVisibleItem.setBackgroundResource(R.drawable.hover)
                                    currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)
                                }
                            }
                        } else {
                            logHoveredCardData(focusedPosition)
                            previousVisibleItem.setBackgroundResource(R.drawable.hover)
                            currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)
                            val distanceToCenter = (recyclerViewServiceList.width - previousVisibleItem.width) / 2
                            recyclerViewServiceList.smoothScrollBy(-distanceToCenter / 10, 0)
                        }
                    }
                    return true
                }
            }

            KeyEvent.KEYCODE_ENTER -> {
                if (isCartZoomed) {
                    // Open RoomServiceReq activity
                    val intent = Intent(this, RoomServiceReq::class.java)
                    startActivity(intent)
                    return true
                } else if (isFirstFocusSet) {
                    val serviceData = getServiceDataAtPosition(focusedPosition)
                    serviceData?.let {
                        val serviceId = it.service_id
                        printLog("ID NYA: $serviceId")

                        if (serviceId != null) {
                            postToCart(serviceId)
                            return true
                        }
                    }
                    return true
                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }


    private fun postToCart(service_id: Int) {

        val macAddress = getMacAddress() ?: ""
        val addToCartFood = AddToCartRequest(macAddress, service_id)

        printLog("Request: $addToCartFood")

        ApiService.endpoint.addToCartService(addToCartFood).enqueue(object : Callback<AddToCartRequest> {
            override fun onResponse(call: Call<AddToCartRequest>, response: Response<AddToCartRequest>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        printLog("POST Response: $responseBody")
                        Toast.makeText(applicationContext, "Success add to cart", Toast.LENGTH_SHORT).show()
                    } else {
                        printLog("POST Response is null.")
                        Toast.makeText(applicationContext, "Failed add to cart, try again", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    printLog("POST Request Failed: ${response.message()}, Code: ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    printLog("Error Body: $errorBody")
                }
            }


            override fun onFailure(call: Call<AddToCartRequest>, t: Throwable) {
                printLog("API Call Failed: ${t.message}")
            }
        })
    }


    private fun getServiceDataAtPosition(position: Int): ServicesData? {
        val recyclerViewService = findViewById<RecyclerView>(R.id.rvthings)
        val adapter = recyclerViewService.adapter as? RecyclerViewServiceAdapter
        return adapter?.getServiceDataAtPosition(position)
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

    private fun getDataApi(){
        val macAddress = getMacAddress()
        if (macAddress != null) {
            printLog("MAC Address: $macAddress")
            ApiService.endpoint.getService(macAddress)
                .enqueue(object : Callback<List<ServicesData>> {
                    override fun onResponse(
                        call: Call<List<ServicesData>>,
                        response: Response<List<ServicesData>>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result != null) {
                                printLog("API Response: $result")
                                serviceList.clear()  // Hapus data sebelum menambahkan yang baru
                                serviceList.addAll(result)
                                recyclerViewServiceAdapter?.setData(result)                            } else {
                                printLog("Response body is null.")
                            }
                        } else {
                            printLog("API Call Failed: ${response.message()}")
                        }
                    }


                    override fun onFailure(call: Call<List<ServicesData>>, t: Throwable) {
                        printLog("API Call Failed: ${t.message}")

                    }
                })
        } else {
            printLog("Unable to obtain MAC address.")

        }


    }

    private fun handleHoveredCard(serviceData: ServicesData) {
        // Tampilkan data layanan ke logcat
        printLog("Hovered Card Data: $serviceData")
    }


    // Fungsi untuk mengirim service_id ke server
    private fun postServiceToCart(serviceId: Int) {
        val macAddress = getMacAddress()
        if (macAddress != null) {
            printLog("Posting service to cart. MAC Address: $macAddress, Service ID: $serviceId")

            val addToCartRequest = AddToCartRequest(mac_address = macAddress, service_id = serviceId)


            ApiService.endpoint.addToCartService(addToCartRequest)
                .enqueue(object : Callback<AddToCartRequest> {
                    override fun onResponse(call: Call<AddToCartRequest>, response: Response<AddToCartRequest>) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result != null) {
                                printLog("POST Response: $result")
                            } else {
                                printLog("Response body is null.")
                            }
                        } else {
                            printLog("POST Request Failed: ${response.message()}, Code: ${response.code()}")
                            val errorBody = response.errorBody()?.string()
                            printLog("Error Body: $errorBody")
                        }
                    }

                    override fun onFailure(call: Call<AddToCartRequest>, t: Throwable) {
                        printLog("POST Request Failed: ${t.message}")
                        t.printStackTrace()
                    }

                })
        } else {
            printLog("Unable to obtain MAC address.")
        }
    }



    private fun printLog(message: String) {
        Log.d(TAG, message)
    }

}