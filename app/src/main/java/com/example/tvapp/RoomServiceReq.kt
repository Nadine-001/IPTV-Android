package com.example.tvapp

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
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

class RoomServiceReq : AppCompatActivity(){

    private val TAG: String = "RoomServiceReq"
    private var isFirstFocusSet: Boolean = false
    private var focusedPosition: Int = 0

    private var recyclerView: RecyclerView? = null
    private var recyclerViewCartServiceAdapter: RecyclerViewCartServiceAdapter? = null
    private var cartList: ArrayList<CartServiceModelItem> = ArrayList()
    private var selectedService: CartServiceModelItem? = null
    private var isDrawableZoomed = false
    private var isCountZoomed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_service_req)
        supportActionBar?.hide()




        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
        val dateFormat = DateFormat.getDateInstance().format(calendar.time)

        val dateTextView = findViewById<TextView>(R.id.text_date)
        dateTextView.text = dateFormat

        val dayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
        val dayTextView = findViewById<TextView>(R.id.text_day)
        val dayOfWeek = dayFormat.format(calendar.time)
        dayTextView.text = dayOfWeek


        cartList = ArrayList()

        recyclerView = findViewById<View>(R.id.rv_cart) as RecyclerView
        recyclerViewCartServiceAdapter = RecyclerViewCartServiceAdapter(ArrayList())
//        recyclerViewCartServiceAdapter = RecyclerViewCartServiceAdapter(ArrayList(), object : RecyclerViewCartServiceAdapter.OnAdapterListener{
//            override fun onClick(cart: CartServiceModelItem) {
//                val itemIdString = cart.item_id.toString()
//                printLog("ID : $itemIdString")
//                recyclerViewCartServiceAdapter?.setSelectedItemId(cart.item_id)
//            }
//
//override fun deleteRequestByItemId(itemId: Int) {
//    val macAddress = getMacAddress()
//    if (macAddress != null) {
//        deleteRequest(itemId, macAddress)
//    } else {
//        printLog("Failed to obtain MAC address.")
//    }
//}
//
//
//        })
        val numberOfColumns = 1
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = recyclerViewCartServiceAdapter

        val sendRequestButton = findViewById<Button>(R.id.welcomeButton2)

        // Menambahkan listener klik ke tombol
        sendRequestButton.setOnClickListener {
            // Memanggil fungsi postDataToServer saat tombol diklik
            postDataToServer(cartList)
        }



    }

    private var isCartZoomed = false
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val recyclerViewServiceList = findViewById<RecyclerView>(R.id.rv_cart)
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

                    val cartView = findViewById<View>(R.id.welcomeButton2)
                    cartView.scaleX = 1.2f
                    cartView.scaleY = 1.2f
                    printLog("Cart view is highlighted")
                    isCartZoomed = true
                    return true
                }
            }

            KeyEvent.KEYCODE_DPAD_UP -> {
//                val holder = recyclerView!!.findViewHolderForAdapterPosition(focusedPosition) as RecyclerViewCartServiceAdapter.MyViewHolder

                if (isCartZoomed) {
                    val cartView = findViewById<View>(R.id.welcomeButton2)
                    cartView.scaleX = 1f
                    cartView.scaleY = 1f

                    val firstVisibleItem = layoutManager.findViewByPosition(focusedPosition)
                    firstVisibleItem?.setBackgroundResource(R.drawable.hover)
                } else if (isDrawableZoomed) {
                    val holder = recyclerView!!.findViewHolderForAdapterPosition(focusedPosition) as? RecyclerViewCartServiceAdapter.MyViewHolder
                    holder?.let {
                        it.drawablebed.scaleX = 1.0f
                        it.drawablebed.scaleY = 1.0f
                    }
                    isDrawableZoomed = false
                    return true
                }
            }

            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                Log.d(TAG, "DPAD_RIGHT pressed")
                if (isDrawableZoomed) {
                    val holder = recyclerView!!.findViewHolderForAdapterPosition(focusedPosition) as? RecyclerViewCartServiceAdapter.MyViewHolder
                    holder?.let {
                        it.drawablebed.scaleX = 1.0f
                        it.drawablebed.scaleY = 1.0f
                        it.elegantNumberButton.scaleX = 1.5f
                        it.elegantNumberButton.scaleY = 1.5f
                        isDrawableZoomed = false
                        isCountZoomed = true
                    }
                    return true
                }
                if (isCountZoomed) {
                    val holder = recyclerView!!.findViewHolderForAdapterPosition(focusedPosition) as? RecyclerViewCartServiceAdapter.MyViewHolder
                    holder?.let {
                        it.elegantNumberButton.scaleX = 1.5f
                        it.elegantNumberButton.scaleY = 1.5f
                        val currentNumber = it.elegantNumberButton.number.toInt()
                        val newNumber = currentNumber + 1
                        it.elegantNumberButton.number = newNumber.toString()
                        cartList[focusedPosition].quantity = newNumber
                    }
                    return true
                }
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
                if (isCountZoomed) {
                    val holder = recyclerView!!.findViewHolderForAdapterPosition(focusedPosition) as? RecyclerViewCartServiceAdapter.MyViewHolder
                    holder?.let {
                        it.elegantNumberButton.scaleX = 1.5f
                        it.elegantNumberButton.scaleY = 1.5f
                        val currentNumber = it.elegantNumberButton.number.toInt()
                        val newNumber = currentNumber - 1
                        if (newNumber >= 0) { // Ensure the number does not go below 0
                            it.elegantNumberButton.number = newNumber.toString()
                            cartList[focusedPosition].quantity = newNumber // Simpan nilai ke cart.quantity
                        }
                    }
                    return true
                }
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
                if (isCartZoomed){
                    postDataToServer(cartList)
                    return true
                }
                if (isCountZoomed) {
                    val holder = recyclerView!!.findViewHolderForAdapterPosition(focusedPosition) as? RecyclerViewCartServiceAdapter.MyViewHolder
                    holder?.let {
                        it.drawablebed.scaleX = 1.0f
                        it.drawablebed.scaleY = 1.0f
                        it.elegantNumberButton.scaleX = 1.0f
                        it.elegantNumberButton.scaleY = 1.0f
                        isDrawableZoomed = false
                        isCountZoomed = false
                    }
                    return true
                }
                if (isFirstFocusSet) {
                    // Ambil posisi item yang sedang difokuskan
                    val focusedView = layoutManager.findViewByPosition(focusedPosition)
                    val cart = cartList.getOrNull(focusedPosition)
                    cart?.let {
                        logHoveredCardData(focusedPosition)
                        val itemId = it.item_id
                        printLog("Clicked Item ID: $itemId")

                        // Set skala drawablebed sesuai dengan item_id yang dipilih
                        val holder = recyclerView!!.findViewHolderForAdapterPosition(focusedPosition) as RecyclerViewCartServiceAdapter.MyViewHolder
                        if (holder != null) {
                            if (!isDrawableZoomed) {
                                // Pertama kali enter untuk zoom drawablebed
                                holder.drawablebed.scaleX = 1.5f
                                holder.drawablebed.scaleY = 1.5f
                                isDrawableZoomed = true
                            } else {
                                // Enter kedua untuk deleteRequest jika sudah di-zoom
                                deleteRequest(itemId, getMacAddress() ?: "")
                                isDrawableZoomed = false // Reset status zoom setelah deleteRequest
                            }
                        }

                        // Lakukan operasi lain sesuai kebutuhan aplikasi Anda
                        // Misalnya, postToCart(itemId) untuk memproses item atau menavigasi ke layar detail
                        recyclerViewCartServiceAdapter?.setSelectedItemId(itemId)

//                        if (holder != null && holder.drawablebed.scaleX == 1.5f && holder.drawablebed.scaleY == 1.5f) {
//                            deleteRequest(itemId, getMacAddress() ?: "")
//                        }
                    }
                    return true
                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    private fun HoveredCardData(position: Int) {
        val cart = cartList.getOrNull(position)
        cart?.let {
            val itemId = it.item_id
            val serviceName = it.room_service_name
            val message = "Hovered over item at position $position: Item ID = $itemId, Service Name = $serviceName"
            printLog(message)
            // Lakukan sesuatu dengan data yang di-log, misalnya, tampilkan pesan atau operasikan data lainnya
        }
    }

    override fun onStart() {
        super.onStart()
        getRoomHeader()
        getDataApi()
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

    private fun getServiceDataAtPosition(position: Int): CartServiceModelItem? {
        val recyclerViewService = findViewById<RecyclerView>(R.id.rv_cart)
        val adapter = recyclerViewService.adapter as? RecyclerViewCartServiceAdapter
        return adapter?.getServiceDataAtPosition(position)
    }

    private fun logHoveredCardData(position: Int) {
        if (position >= 0 && position < cartList.size) {
            val hoveredCardData = cartList[position]
            printLog("Hovered Card Data: $hoveredCardData")
            handleHoveredCard(hoveredCardData)
        } else {
            printLog("Invalid position or serviceList is empty. Unable to retrieve Hovered Card Data for position: $position")
        }

    }
    private fun handleHoveredCard(serviceData: CartServiceModelItem) {
        // Tampilkan data layanan ke logcat
        printLog("Hovered Card Data: $serviceData")
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
            ApiService.endpoint.getCartServiceList(macAddress)
                .enqueue(object : Callback<List<CartServiceModelItem>> {
                    override fun onResponse(
                        call: Call<List<CartServiceModelItem>>,
                        response: Response<List<CartServiceModelItem>>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result != null) {
                                printLog("API Response: $result")



                                cartList.clear()  // Hapus data sebelum menambahkan yang baru
                                cartList.addAll(result)
                                recyclerViewCartServiceAdapter?.setData(result)                            } else {
                                printLog("Response body is null.")
                            }
                        } else {
                            printLog("API Call Failed: ${response.message()}")
                        }
                    }


                    override fun onFailure(call: Call<List<CartServiceModelItem>>, t: Throwable) {
                        printLog("API Call Failed: ${t.message}")

                    }
                })
        } else {
            printLog("Unable to obtain MAC address.")

        }


    }

    private fun postDataToServer(cartItems: List<CartServiceModelItem>) {
        // Mengambil MAC Address
        val macAddress = getMacAddress()
        SocketHandler.setSocket()
        SocketHandler.establishConnection()
        val mSocket = SocketHandler.getSocket()

        // Mengecek apakah MAC Address berhasil didapatkan
        if (macAddress != null) {
            // Membuat objek PostReqServiceModel
            mSocket.emit("television", macAddress)
            val postReqServiceModel = PostReqServiceModel(macAddress, ArrayList())

            for (cartItem in cartItems) {
                val request = Request(cartItem.item_id, cartItem.quantity, cartItem.room_service_id)
                postReqServiceModel.requests.add(request) // Menggunakan metode add pada MutableList
            }


            // Mengirimkan data ke server
            ApiService.endpoint.postReqService(postReqServiceModel)
                .enqueue(object : Callback<PostReqServiceModel> {
                    override fun onResponse(
                        call: Call<PostReqServiceModel>,
                        response: Response<PostReqServiceModel>
                    ) {
                        if (response.isSuccessful) {
                            // Berhasil melakukan POST
                            printLog("Data successfully posted to server: $response")
                            Toast.makeText(applicationContext, "Success add request. Please Waiting, your request will be process with our staff", Toast.LENGTH_SHORT).show()

                            val intent = Intent(applicationContext, RoomServiceDetail::class.java)
                            startActivity(intent)
                        } else {
                            // Gagal melakukan POST
                            printLog("Failed to post data to server: ${response.message()}")
                            Toast.makeText(applicationContext, "Failed send request. Please try again", Toast.LENGTH_SHORT).show()

                        }
                    }

                    override fun onFailure(call: Call<PostReqServiceModel>, t: Throwable) {
                        // Gagal melakukan request
                        printLog("Failed to make POST request: ${t.message}")
                    }
                })
        } else {
            // Jika MAC Address tidak berhasil didapatkan
            printLog("Unable to obtain MAC address.")
        }
    }


//    private fun deleteRequestByItemId(mac_address: String, item_id: Int) {
//        ApiService.endpoint.deleteRequest(item_id, mac_address)
//            .enqueue(object : Callback<String> {
//                override fun onResponse(call: Call<String>, response: Response<String>) {
//                    if (response.isSuccessful) {
//                        // Berhasil menghapus permintaan
//                        printLog("Request with item_id $item_id successfully deleted")
//                        Toast.makeText(applicationContext, "Request successfully deleted", Toast.LENGTH_SHORT).show()
//
//                        // Refresh data setelah menghapus
//                        getDataApi()
//                    } else {
//                        // Gagal menghapus permintaan
//                        printLog("Failed to delete request: ${response.message()}")
//                        Toast.makeText(applicationContext, "Failed to delete request. Please try again", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<String>, t: Throwable) {
//                    // Gagal melakukan request
//                    printLog("Failed to make DELETE request: ${t.message}")
//                    Toast.makeText(applicationContext, "Failed to delete request. Please try again", Toast.LENGTH_SHORT).show()
//                }
//            })
//    }


//    private fun deleteRequestByItemId(itemId: Int) {
//        val macAddress = getMacAddress()
//        if (macAddress != null) {
//            deleteRequestByItemId(itemId)
//        } else {
//            printLog("Failed to obtain MAC address.")
//        }
//    }
//override fun onClick(cart: CartServiceModelItem) {
//    // ...
//}
//    override fun deleteRequestByItemId(itemId: Int) {
//        // Lakukan tindakan yang sesuai saat item dihapus
//        // Misalnya, Anda dapat menampilkan item_id di logcat seperti yang diminta
//        printLog("Item deleted. Item ID: $itemId")
//    }
//private fun deleteRequest(itemId: Int) {
//    val macAddress = getMacAddress()
//    if (macAddress != null) {
//        ApiService.endpoint.deleteRequest(itemId, macAddress)
//            .enqueue(object : Callback<String> {
//                override fun onResponse(call: Call<String>, response: Response<String>) {
//                    if (response.isSuccessful) {
//                        // Berhasil menghapus permintaan
//                        printLog("Request with item_id $itemId successfully deleted")
//                        Toast.makeText(applicationContext, "Request successfully deleted", Toast.LENGTH_SHORT).show()
//
//                        // Refresh data setelah menghapus
//                        getDataApi()
//                    } else {
//                        // Gagal menghapus permintaan
//                        printLog("Failed to delete request: ${response.message()}")
//                        Toast.makeText(applicationContext, "Failed to delete request. Please try again", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<String>, t: Throwable) {
//                    // Gagal melakukan request
//                    printLog("Failed to make DELETE request: ${t.message}")
//                    Toast.makeText(applicationContext, "Failed to delete request. Please try again", Toast.LENGTH_SHORT).show()
//                }
//            })
//    } else {
//        printLog("Failed to obtain MAC address.")
//    }
//
//}
//    override fun onClick(cart: CartServiceModelItem) {
//        // Implementasi tindakan yang diambil saat item diklik
//        Toast.makeText(applicationContext, "Item clicked: ${cart.item_id}", Toast.LENGTH_SHORT).show()
//    }
    private fun printLog(message: String) {
        Log.d(TAG, message)
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

    private fun deleteRequest(itemId: Int, macAddress: String) {
        val macAddress = getMacAddress()
        if (macAddress != null) {
            ApiService.endpoint.deleteRequest(itemId, macAddress)
                .enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            // Berhasil menghapus permintaan
                            printLog("Request with item_id $itemId successfully deleted")
                            Toast.makeText(applicationContext, "Request successfully deleted", Toast.LENGTH_SHORT).show()

                            // Refresh data setelah menghapus
                            getDataApi()
                        } else {
                            // Gagal menghapus permintaan
                            printLog("Failed to delete request: ${response.message()}")
                            Toast.makeText(applicationContext, "Failed to delete request. Please try again", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        // Gagal melakukan request
                        printLog("Failed to make DELETE request: ${t.message}")
                        Toast.makeText(applicationContext, "Failed to delete request. Please try again", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            printLog("Failed to obtain MAC address.")
        }

    }
//    override fun deleteRequestByItemId(itemId: Int) {
//        val macAddress = getMacAddress()
//        if (macAddress != null) {
//            deleteRequest(itemId, macAddress)
//        } else {
//            printLog("Failed to obtain MAC address.")
//        }
//        // Lakukan tindakan yang sesuai saat item dihapus
//        // Misalnya, Anda dapat menampilkan item_id di logcat seperti yang diminta
//        printLog("Item deleted. Item ID: $itemId")
////        deleteRequest(itemId, macAddress)
//    }
//    override fun onClick(cart: CartServiceModelItem) {
//        // Implementasi tindakan yang diambil saat item diklik
//        Toast.makeText(applicationContext, "Item clicked: ${cart.item_id}", Toast.LENGTH_SHORT).show()
    }
