package com.example.tvapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.tvapp.retrofit.ApiService
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class OrderFoodPayment : AppCompatActivity() {
    private val TAG: String = "OrderFoodPayment"
//    private var serviceCartModel: ArrayList<OrderX> = ArrayList()

    private var databaseId: Int? = null
    private var recyclerView: RecyclerView? = null
    private var recyclerViewCartMenuAdapter: RecyclerViewCartMenuAdapter? = null
    private var cartList: ArrayList<OrderX> = ArrayList()
    private var selectedService: OrderX? = null
    private var paymentDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_food_payment)
//        serviceCartModel = getDataApi()

        supportActionBar?.hide()

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
        val dateFormat = DateFormat.getDateInstance().format(calendar.time)

        val dateTextView = findViewById<TextView>(R.id.text_date)
        dateTextView.text = dateFormat

        val dayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
        val dayTextView = findViewById<TextView>(R.id.text_day)
        val dayOfWeek = dayFormat.format(calendar.time)
        dayTextView.text = dayOfWeek

        val welcomeButton3 = findViewById<Button>(R.id.welcomeButton3)
        welcomeButton3.visibility = View.GONE

//        val total1 = findViewById<TextView>(R.id.total1)
//        val totalPrice = calculateTotalPrice(cartList)
//        total1.text = "Total Price: $totalPrice"

        cartList = ArrayList()

        recyclerView = findViewById<View>(R.id.rv_cart_food) as RecyclerView
        recyclerViewCartMenuAdapter = RecyclerViewCartMenuAdapter(ArrayList())

//        recyclerViewCartMenuAdapter = RecyclerViewCartMenuAdapter(ArrayList(), object : RecyclerViewCartMenuAdapter.OnAdapterListener{
//            override fun onClick(cart: OrderX) {
//                Toast.makeText(applicationContext, cart.item_id, Toast.LENGTH_SHORT).show()
//            }
//            override fun deleteRequestByItemId(itemId: Int) {
//                val macAddress = getMacAddress()
//                if (macAddress != null) {
//                    deleteRequest(itemId, macAddress)
//                } else {
//                    printLog("Failed to obtain MAC address.")
//                }
//            }
//
//            override fun updateTotalPrice(totalPrice: Int) {
//                val total1 = findViewById<TextView>(R.id.total1)
//                total1.text = totalPrice.toString()
//            }
//
//
//        })

        val numberOfColumns = 1
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = recyclerViewCartMenuAdapter

        val sendRequestButton = findViewById<Button>(R.id.welcomeButton2)

//        val total1 = findViewById<TextView>(R.id.total1)


        sendRequestButton.setOnClickListener {

            showPaymentMethodPopup()

        }

        val showQRAgain = findViewById<Button>(R.id.welcomeButton3)
        showQRAgain.setOnClickListener {
            // Ensure databaseId is not null before using it
            if (databaseId != null && databaseId != -1) {
                // Safe call to ensure databaseId is non-null
                val dialog = ScanQRAgain(this, databaseId!!)

                // Print the databaseId
                Log.d("DatabaseId", "Value of databaseId: $databaseId")

                // Show the dialog
                dialog.show()
            } else {
                Log.d("DatabaseId", "Invalid databaseId: $databaseId")
            }
        }




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

//    private fun getCartPosition(position: Int): OrderX? {
//        val recyclerViewCart = findViewById<RecyclerView>(R.id.rv_cart_food)
//        val adapter = recyclerViewCart.adapter as? RecyclerViewCartMenuAdapter
//        return adapter?.getCartPosition(position)
//    }

    private fun handleHoveredCard(cartData: OrderX) {
        printLog("Hovered Card Data: $cartData")
    }

    private var isFirstFocusSet: Boolean = false
    private var focusedPositionCart: Int = 0
    private var isEnd: Boolean = false
    private var focusedPosition: Int = 0
    private var isCartZoomed = false
    private var isDrawableZoomed = false
    private var isCountZoomed = false
    private var isShowAgainZoomed = false
    private var orderZoom : Boolean = false

    private fun getCartPosition(position: Int): OrderX? {
        val recyclerViewCart = findViewById<RecyclerView>(R.id.rv_cart_food)
        val adapter = recyclerViewCart.adapter as? RecyclerViewCartMenuAdapter
        return adapter?.getCartPosition(position)
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val recyclerViewServiceList = findViewById<RecyclerView>(R.id.rv_cart_food)
        val layoutManager = recyclerViewServiceList.layoutManager as? LinearLayoutManager
        val sendRequestButton = findViewById<Button>(R.id.welcomeButton2)

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
                if (paymentDialog?.isShowing == true) {
                    Log.d(TAG, "Payment dialog is showing")
                    val cashButton = paymentDialog?.findViewById<View>(R.id.cashButton)
                    if (cashButton != null) {
                        cashButton.scaleX = 1.2f
                        cashButton.scaleY = 1.2f
                        Log.d(TAG, "Cash button is highlighted")
                    } else {
                        Log.d(TAG, "Cash button not found in dialog")
                    }
                    return true
                } else {
                    Log.d(TAG, "Payment dialog is not showing")
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
                    val holder = recyclerView!!.findViewHolderForAdapterPosition(focusedPosition) as? RecyclerViewCartMenuAdapter.MyViewHolder
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
                    val holder = recyclerView!!.findViewHolderForAdapterPosition(focusedPosition) as? RecyclerViewCartMenuAdapter.MyViewHolder
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
                    val holder = recyclerView!!.findViewHolderForAdapterPosition(focusedPosition) as? RecyclerViewCartMenuAdapter.MyViewHolder
                    holder?.let {
                        it.elegantNumberButton.scaleX = 1.5f
                        it.elegantNumberButton.scaleY = 1.5f
                        val currentNumber = it.elegantNumberButton.number.toInt()
                        val newNumber = currentNumber + 1
                        it.elegantNumberButton.number = newNumber.toString()
                        cartList[focusedPosition].quantity = newNumber

                        val total1 = findViewById<TextView>(R.id.total1)
                        val totalPrice = calculateTotalPrice(cartList)
                        total1.text = "$totalPrice"
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
                val welcomeButton3 = findViewById<Button>(R.id.welcomeButton3)
                val cartView = findViewById<View>(R.id.welcomeButton2)
                Log.d(TAG, "DPAD_LEFT pressed")
                if (welcomeButton3.visibility == View.VISIBLE) {
                    welcomeButton3.scaleX = 1.2f
                    welcomeButton3.scaleY = 1.2f

                    cartView.scaleX = 1.0f
                    cartView.scaleY = 1.0f
                    isShowAgainZoomed = true
                }
                if (isCountZoomed) {
                    val holder = recyclerView!!.findViewHolderForAdapterPosition(focusedPosition) as? RecyclerViewCartMenuAdapter.MyViewHolder
                    holder?.let {
                        it.elegantNumberButton.scaleX = 1.5f
                        it.elegantNumberButton.scaleY = 1.5f
                        val currentNumber = it.elegantNumberButton.number.toInt()
                        val newNumber = currentNumber - 1
                        if (newNumber >= 0) { // Ensure the number does not go below 0
                            it.elegantNumberButton.number = newNumber.toString()
                            cartList[focusedPosition].quantity = newNumber // Simpan nilai ke cart.quantity
                        }
                        val total1 = findViewById<TextView>(R.id.total1)
                        val totalPrice = calculateTotalPrice(cartList)
                        total1.text = "$totalPrice"
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
                if(isShowAgainZoomed){
                    if (databaseId != null && databaseId != -1) {
                        // Safe call to ensure databaseId is non-null
                        val dialog = ScanQRAgain(this, databaseId!!)

                        // Print the databaseId
                        Log.d("DatabaseId", "Value of databaseId: $databaseId")

                        // Show the dialog
                        dialog.show()
                    } else {
                        Log.d("DatabaseId", "Invalid databaseId: $databaseId")
                    }
                    return true
                }
                if (isCartZoomed){
                    showPaymentMethodPopup()
                    return true
                }
                if (isCountZoomed) {
                    val holder = recyclerView!!.findViewHolderForAdapterPosition(focusedPosition) as? RecyclerViewCartMenuAdapter.MyViewHolder
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
                    val cart = getCartPosition(focusedPosition)
                    cart?.let {
                        logHoveredCardData(focusedPosition)
                        val itemId = it.item_id
                        printLog("Clicked Item ID: $itemId")

                        // Set skala drawablebed sesuai dengan item_id yang dipilih
                        val holder = recyclerView!!.findViewHolderForAdapterPosition(focusedPosition) as RecyclerViewCartMenuAdapter.MyViewHolder
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
                        recyclerViewCartMenuAdapter?.setSelectedItemId(itemId)

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


//    override fun updateTotalPrice(totalPrice: Int) {
//        val total1 = findViewById<TextView>(R.id.total1)
//        total1.text = totalPrice.toString()
//    }

    private var isCashButtonHovered = false
    private var selectedPaymentMethod = "Cash"
    private fun showPaymentMethodPopup() {
        paymentDialog = Dialog(this)
        paymentDialog?.setContentView(R.layout.payment)
        val radioGroup = paymentDialog?.findViewById<RadioGroup>(R.id.radioGroup)

        // Menangani KeyEvent
        paymentDialog?.setOnKeyListener { dialog, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        Log.d("MyAppTag", "DPAD_DOWN pressed")

                        val cashButton = paymentDialog?.findViewById<View>(R.id.cashButton)
                        val scanButton = paymentDialog?.findViewById<View>(R.id.scanButton)

                        if (isCashButtonHovered) {
                            // Pindahkan hover ke scanButton
                            cashButton?.setBackgroundResource(0) // Menghapus hover dari cashButton
                            scanButton?.setBackgroundResource(R.drawable.hover) // Mengatur hover ke scanButton
                            isCashButtonHovered = false
                            selectedPaymentMethod = "Scan QR"
                            Log.d("MyAppTag", "Scan button is highlighted")
                        } else {
                            // Hover pada cashButton
                            cashButton?.setBackgroundResource(R.drawable.hover)
                            scanButton?.setBackgroundResource(0) // Menghapus hover dari scanButton
                            isCashButtonHovered = true
                            selectedPaymentMethod = "Cash"
                            Log.d("MyAppTag", "Cash button is highlighted")
                        }
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_ENTER -> {
                        Log.d("MyAppTag", "ENTER pressed")

                        if (selectedPaymentMethod.isNotEmpty()) {
                            postDataToServer(cartList, selectedPaymentMethod)
                            paymentDialog?.dismiss()
                            Log.d("MyAppTag", "Data posted with payment method: $selectedPaymentMethod")
                        } else {
                            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
                            Log.d("MyAppTag", "No payment method selected")
                        }
                        return@setOnKeyListener true
                    }
                }
            }
            false
        }

        radioGroup?.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = paymentDialog?.findViewById<RadioButton>(checkedId)
            val paymentMethod = radioButton?.text.toString()
            if (paymentMethod.isNotEmpty()) {
                postDataToServer(cartList, paymentMethod)
                paymentDialog?.dismiss()
            } else {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
            }
        }
        paymentDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        paymentDialog?.setCancelable(true)
        paymentDialog?.show()
    }


    override fun onStart() {
        super.onStart()
        getRoomHeader()
        getDataApi()
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

    private fun calculateTotalPrice(orderList: List<OrderX>): Int {
        var totalPrice = 0
        for (order in orderList) {
            totalPrice += order.menu_price * order.quantity
        }
        return totalPrice
    }

    private fun getDataApi() {
        val macAddress = getMacAddress()
        if (macAddress != null) {
            ApiService.endpoint.getCartMenuList(macAddress)
                .enqueue(object : Callback<List<OrderX>> {
                    override fun onResponse(
                        call: Call<List<OrderX>>,
                        response: Response<List<OrderX>>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result != null) {
                                printLog("API Response: $result")
                                cartList.clear()  // Hapus data sebelum menambahkan yang baru
                                cartList.addAll(result)
                                recyclerViewCartMenuAdapter?.setData(cartList)
                                val totalPrice = calculateTotalPrice(cartList)
                                val total1 = findViewById<TextView>(R.id.total1)
                                total1.text = "$totalPrice"
                            } else {
                                printLog("Response body is null.")


                            }
                        } else {
                            printLog("API Call Failed: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<List<OrderX>>, t: Throwable) {
                        printLog("API Call Failed: ${t.message}")
                    }
                })
        } else {
            printLog("Unable to obtain MAC address.")
        }
    }

    private fun printLog(message: String) {
        Log.d(TAG, message)
    }

    private fun deleteRequest(itemId: Int, macAddress: String) {
        val macAddress = getMacAddress()
        if (macAddress != null) {
            ApiService.endpoint.deleteRequestMenu(itemId, macAddress)
                .enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {

                            printLog("Request with item_id $itemId successfully deleted")
                            Toast.makeText(applicationContext, "Request successfully deleted", Toast.LENGTH_SHORT).show()

                            getDataApi()
                        } else {
                            printLog("Failed to delete request: ${response.message()}")
                            Toast.makeText(applicationContext, "Failed to delete request. Please try again", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        printLog("Failed to make DELETE request: ${t.message}")
                        Toast.makeText(applicationContext, "Failed to delete request. Please try again", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            printLog("Failed to obtain MAC address.")
        }

    }

    private fun postDataToServer(serviceCartModels: List<OrderX>, paymentMethod: String) {
        val macAddress = getMacAddress()
        SocketHandler.setSocket()
        SocketHandler.establishConnection(this)
        val mSocket = SocketHandler.getSocket()
        if (macAddress != null) {
            val macAddressObject = JSONObject()
            macAddressObject.put("mac_address", macAddress)

            // Emit MAC Address sebagai object dengan event "television"
            Log.d("Socket", "Emitting MAC Address: $macAddressObject")
            mSocket.emit("television", macAddressObject)
            val total1 = findViewById<TextView>(R.id.total1)
            val totalPrice = total1.text.toString().toIntOrNull() ?: 0

            val postOrderFoodModel = PostOrderFoodModel(macAddress, ArrayList(), paymentMethod, totalPrice)

            // Asumsikan serviceCartModel adalah daftar item
//            val ordersList = mutableListOf<Order>()

            for (serviceCartModel in serviceCartModels) {
                val order = Order(serviceCartModel.item_id, serviceCartModel.menu_id, serviceCartModel.quantity)
                postOrderFoodModel.orders.add(order)
            }


            ApiService.endpoint.postReqFood(postOrderFoodModel)
                .enqueue(object : Callback<PostOrderResponse> {
                    override fun onResponse(call: Call<PostOrderResponse>, response: Response<PostOrderResponse>) {
                        printLog("Data to be sent to server: $postOrderFoodModel")
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null) {
                                val orderId = responseBody.order_id
                                val paymentUrl = responseBody.payment_url
                                val databaseId = responseBody.database_id
                                printLog("Order ID: $orderId, Payment URL: $paymentUrl, DATABASE ID: $databaseId")

                                this@OrderFoodPayment.databaseId = databaseId

                                if (orderId != null && paymentUrl != null && databaseId != null) {
                                    val intent = Intent(applicationContext, ScanQR::class.java)
                                    intent.putExtra("payment_url", paymentUrl)
                                    intent.putExtra("order_id", orderId)
                                    intent.putExtra("database_id", databaseId)
                                    val dialog = ScanQR(this@OrderFoodPayment, paymentUrl, orderId, databaseId)
                                    dialog.show()
                                } else {
                                    Toast.makeText(applicationContext, "Success add request. Please Waiting, your request will be processed with our staff", Toast.LENGTH_SHORT).show()

                                    val intent = Intent(applicationContext, com.example.tvapp.OrderFoodNew::class.java)
                                    startActivity(intent)
                                }

                                val welcomeButton3 = findViewById<Button>(R.id.welcomeButton3)
                                welcomeButton3.visibility = View.VISIBLE
                                getDataApi()

                            } else {
                                printLog("Failed to get response body.")
                                Toast.makeText(applicationContext, "Failed send request. Please try again", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            printLog("Failed to post data to server: ${response.message()}")
                            Toast.makeText(applicationContext, "Failed send request. Please try again", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<PostOrderResponse>, t: Throwable) {
                        printLog("Failed to make POST request: ${t.message}")
                    }
                })
        } else {
            printLog("Unable to obtain MAC address.")
        }
    }//    override fun deleteRequestByItemId(itemId: Int) {
//        val macAddress = getMacAddress()
//        if (macAddress != null) {
//            deleteRequest(itemId, macAddress)
//        } else {
//            printLog("Failed to obtain MAC address.")
//        }
//        printLog("Item deleted. Item ID: $itemId")
//    }
//    override fun onClick(cart: OrderX) {
//        Toast.makeText(applicationContext, "Item clicked: ${cart.item_id}", Toast.LENGTH_SHORT).show()
//    }
}