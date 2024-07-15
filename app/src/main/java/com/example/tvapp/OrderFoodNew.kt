package com.example.tvapp

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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

class OrderFoodNew : AppCompatActivity() {
    private val TAG: String = "OrderFood"
    private var isFirstFocusSet: Boolean = false
    private var focusedPosition: Int = 0


    private var menuData: List<MenuType>? = null
    private var allDataLoaded: Boolean = false
    private var recyclerViewDaftarMenuAdapter: RecyclerViewDaftarMenuAdapter? = null
    private var menuList2: ArrayList<DaftarMenuPerCategoryModelItem> = ArrayList()
    private var categoryId: Int = -1
    private var recyclerView: RecyclerView? = null
    private var recyclerViewCategoryAdapter: RecyclerViewCategoryAdapter? = null
    private var menuList: ArrayList<MenuType> = ArrayList()
    private var currentCall: Call<List<DaftarMenuPerCategoryModelItem>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_food_new)
        supportActionBar?.hide()

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
        val dateFormat = DateFormat.getDateInstance().format(calendar.time)

        val dateTextView = findViewById<TextView>(R.id.text_date)
        dateTextView.text = dateFormat

        val dayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
        val dayTextView = findViewById<TextView>(R.id.text_day)
        val dayOfWeek = dayFormat.format(calendar.time)
        dayTextView.text = dayOfWeek

        menuList = ArrayList()
        recyclerView = findViewById<View>(R.id.rv_food) as RecyclerView
        recyclerViewCategoryAdapter = RecyclerViewCategoryAdapter(ArrayList())

        menuList2 = ArrayList()
        recyclerView = findViewById<View>(R.id.rv_food1) as RecyclerView
        recyclerViewDaftarMenuAdapter = RecyclerViewDaftarMenuAdapter(ArrayList(), object : RecyclerViewDaftarMenuAdapter.OnAdapterListener {
            override fun onClick(service: DaftarMenuPerCategoryModelItem) {
                Toast.makeText(applicationContext, service.menu_name, Toast.LENGTH_SHORT).show()
            }

            override fun addToCartClicked(view: View) {
                val parentLayout = view.parent as View
                val rvServiceTxt = parentLayout.findViewById<TextView>(R.id.menu_name)
                val menuName = rvServiceTxt.text.toString()

                val menuId = menuList2.firstOrNull { it.menu_name == menuName }?.menu_id

                menuId?.let {
                    printLog("Service ID for $menuName: $menuId")
                    val macAddress = getMacAddress() ?: ""
                    val addToCartFood = AddFoodToCartRequest(macAddress, it)

                    ApiService.endpoint.addToCartFood(addToCartFood)
                        .enqueue(object : Callback<AddFoodToCartRequest> {
                            override fun onResponse(call: Call<AddFoodToCartRequest>, response: Response<AddFoodToCartRequest>) {
                                if (response.isSuccessful) {
                                    val result = response.body()
                                    if (result != null) {
                                        printLog("POST Response: ${response.message()}")
                                        Toast.makeText(applicationContext, "Success add to cart: $menuName", Toast.LENGTH_SHORT).show()
                                    } else {
                                        printLog("Response body is null.")
                                        Toast.makeText(applicationContext, "Failed add to cart, try again", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(applicationContext, "You need to complete the payment of previous order first.", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@OrderFoodNew, PendingTransaksi::class.java)
                                    startActivity(intent)
                                    printLog("POST Request Failed: ${response.message()}, Code: ${response.code()}")
                                    val errorBody = response.errorBody()?.string()
                                    printLog("Error Body: $errorBody")
                                }
                            }

                            override fun onFailure(call: Call<AddFoodToCartRequest>, t: Throwable) {
                                printLog("POST Request Failed: ${t.message}")
                                t.printStackTrace()
                            }
                        })
                } ?: run {
                    printLog("Service ID not found for $menuName")
                }
            }
        })
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView = findViewById<View>(R.id.rv_food) as RecyclerView
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = recyclerViewCategoryAdapter

        val layoutManager2: RecyclerView.LayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView = findViewById<View>(R.id.rv_food1) as RecyclerView
        recyclerView!!.layoutManager = layoutManager2
        recyclerView!!.adapter = recyclerViewDaftarMenuAdapter

        getRoomHeader()
        getDataApi()
//        getDataFromApi()

        val cartButton = findViewById<View>(R.id.cart)
        cartButton.setOnClickListener {
            val intent = Intent(this, OrderFoodPayment::class.java)
            startActivity(intent)
        }
    }

//    private fun updateCategoryId(newCategoryId: Int) {
//        categoryId = newCategoryId
//        getDataApi2()
//    }

//    private fun addToCartClicked(view: View) {
//        val parentLayout = view.parent as View
//        val rvServiceTxt = parentLayout.findViewById<TextView>(R.id.m enu_name)
//        val menuName = rvServiceTxt.text.toString()
//
//        val menuId = menuList2.firstOrNull { it.menu_name == menuName }?.menu_id
//
//        menuId?.let {
//            printLog("Service ID for $menuName: $menuId")
//            val macAddress = getMacAddress() ?: ""
//            val addToCartFood = AddFoodToCartRequest(macAddress, it)
//
//            ApiService.endpoint.addToCartFood(addToCartFood)
//                .enqueue(object : Callback<AddFoodToCartRequest> {
//                    override fun onResponse(call: Call<AddFoodToCartRequest>, response: Response<AddFoodToCartRequest>) {
//                        if (response.isSuccessful) {
//                            val result = response.body()
//                            if (result != null) {
//                                printLog("POST Response: ${response.message()}")
//                                Toast.makeText(applicationContext, "Success add to cart: $menuName", Toast.LENGTH_SHORT).show()
//                            } else {
//                                printLog("Response body is null.")
//                                Toast.makeText(applicationContext, "Failed add to cart, try again", Toast.LENGTH_SHORT).show()
//                            }
//                        } else {
//                            Toast.makeText(applicationContext, "You need to complete the payment of previous order first.", Toast.LENGTH_SHORT).show()
//                            val intent = Intent(this@OrderFoodNew, PendingTransaksi::class.java)
//                            startActivity(intent)
//                            printLog("POST Request Failed: ${response.message()}, Code: ${response.code()}")
//                            val errorBody = response.errorBody()?.string()
//                            printLog("Error Body: $errorBody")
//                        }
//                    }
//
//                    override fun onFailure(call: Call<AddFoodToCartRequest>, t: Throwable) {
//                        printLog("POST Request Failed: ${t.message}")
//                        t.printStackTrace()
//                    }
//                })
//        } ?: run {
//            printLog("Service ID not found for $menuName")
//        }
//    }


    private fun getMacAddress(): String? {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        val wifiInfo = wifiManager?.connectionInfo
        return wifiInfo?.macAddress
    }

    private fun getRoomHeader() {
        val macAddress = getMacAddress()
        if (macAddress != null) {
            ApiService.endpoint.getHeader(macAddress)
                .enqueue(object : Callback<RoomHeaderModel> {
                    override fun onResponse(call: Call<RoomHeaderModel>, response: Response<RoomHeaderModel>) {
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
            printLog("MAC Address: $macAddress")
            ApiService.endpoint.getHeaderFood(macAddress)
                .enqueue(object : Callback<AdsModel> {
                    override fun onResponse(call: Call<AdsModel>, response: Response<AdsModel>) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result != null) {
                                val orderFoodIntro = findViewById<TextView>(R.id.order_food_intro)
                                val htmlContent = result.order_food_intro
                                val spannedHtmlContent = if (htmlContent != null && htmlContent.isNotEmpty()) {
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                        Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY)
                                    } else {
                                        @Suppress("DEPRECATION")
                                        Html.fromHtml(htmlContent)
                                    }
                                } else {
                                    SpannableString("")
                                }
                                orderFoodIntro.text = spannedHtmlContent
                                printLog("API Response: $result")
                            } else {
                                printLog("Response body is null.")
                            }
                        } else {
                            printLog("API Call Failed: ${response.message()}")
                        }

//                        printLog(result.toString())
                    }

                    override fun onFailure(call: Call<AdsModel>, t: Throwable) {
                        printLog((t.toString()))
                    }
                })
        } else {
            printLog("Unable to obtain MAC address.")
        }
    }


    private fun printLog(message: String) {
        Log.d(TAG, message)
    }

    private fun getDataApi() {
        val macAddress = getMacAddress()
        if (macAddress != null) {
            printLog("MAC Address: $macAddress")
            ApiService.endpoint.getCategoryList(macAddress)
                .enqueue(object : Callback<List<MenuType>> {
                    override fun onResponse(call: Call<List<MenuType>>, response: Response<List<MenuType>>) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result != null) {
                                printLog("API Response: $result")
                                if (result.isNotEmpty() && result[0].menu_type != null) {
                                    menuList.clear()
                                    menuList.addAll(result)
                                    recyclerViewCategoryAdapter?.setData(result)

                                    if (menuList.isNotEmpty()) {
                                        categoryId = menuList[0].menu_type_id
                                        getDataApi2(categoryId)
                                    }
                                } else {
                                    printLog("Response body contains null menu_type.")
                                    displayMessage("We're sorry, our kitchen is not yet ready to take an order.")
                                }
                            } else {
                                printLog("Response body is null.")
                                displayMessage("Failed to retrieve data from server.")
                            }
                        } else {
                            printLog("API Call Failed: ${response.message()}")
                            displayMessage("API call failed: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<List<MenuType>>, t: Throwable) {
                        printLog(t.toString())
                        displayMessage("API request failed: ${t.message}")
                    }
                })
        }
    }

    private fun displayMessage(message: String) {
        val messageTextView: TextView = findViewById(R.id.messages)
        messageTextView.text = message
    }



    private fun getDataApi2(categoryId: Int) {
        currentCall?.cancel()
        val macAddress = getMacAddress()
        if (macAddress != null) {
            printLog("MAC Address: $macAddress")

            if (categoryId != -1) {
                currentCall = ApiService.endpoint.getMenuList(macAddress, categoryId)
                currentCall?.enqueue(object : Callback<List<DaftarMenuPerCategoryModelItem>> {
                    override fun onResponse(call: Call<List<DaftarMenuPerCategoryModelItem>>, response: Response<List<DaftarMenuPerCategoryModelItem>>) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result != null) {
                                printLog("API Response: $result")
                                menuList2.clear()
                                menuList2.addAll(result)
                                recyclerViewDaftarMenuAdapter?.setData(result)
                                recyclerViewDaftarMenuAdapter?.notifyDataSetChanged()
                            } else {
                                printLog("Response body is null.")
                            }
                        } else {
                            printLog("API Call Failed: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<List<DaftarMenuPerCategoryModelItem>>, t: Throwable) {
                        printLog((t.toString()))
                    }
                })
            } else {
                printLog("Invalid categoryId")
            }
        }
    }
    private var isCartZoomed = false
private var focusedPositionCategory = -1
    private var focusedPositionMenu = -1

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val cartIcon = findViewById<ImageView>(R.id.cartIcon)
        val cartView = findViewById<View>(R.id.cart)
        val recyclerViewCategory = findViewById<RecyclerView>(R.id.rv_food)
        val recyclerViewMenu = findViewById<RecyclerView>(R.id.rv_food1)
        val layoutManagerCategory = recyclerViewCategory.layoutManager as? LinearLayoutManager
        val layoutManagerMenu = recyclerViewMenu.layoutManager as? LinearLayoutManager

        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (!isFirstFocusSet) {
                    layoutManagerCategory?.findViewByPosition(0)?.let { firstVisibleItem ->
                        firstVisibleItem.setBackgroundResource(R.drawable.hoverb)
                        logHoveredCardData(0)
                        isFirstFocusSet = true
                        focusedPositionCategory = 0
                    }
                    return true
                } else if (isFirstFocusSet && focusedPositionCategory != -1) {
                    layoutManagerCategory?.findViewByPosition(focusedPositionCategory)?.setBackgroundResource(R.drawable.hoverbb)
                    focusedPositionCategory = -1

                    val nextFocusPosition = 0
                    layoutManagerMenu?.findViewByPosition(nextFocusPosition)?.let { nextVisibleItem ->
                        nextVisibleItem.setBackgroundResource(R.drawable.hoverb)
                        logHoveredCardDataM(nextFocusPosition)
                        focusedPositionMenu = nextFocusPosition
                    }
                    return true
                } else if (isFirstFocusSet && focusedPositionMenu != -1) {
                    layoutManagerMenu?.findViewByPosition(focusedPositionMenu)?.setBackgroundResource(R.drawable.hoverbb)
                    focusedPositionMenu = -1

                    cartView.scaleX = 1.2f
                    cartView.scaleY = 1.2f
                    printLog("Cart view is highlighted and zoomed")
                    isCartZoomed = true
                    return true
                }
            }




            KeyEvent.KEYCODE_DPAD_UP -> {
                if (focusedPositionMenu != -1) {
                    layoutManagerMenu?.findViewByPosition(focusedPositionMenu)?.setBackgroundResource(R.drawable.hoverbb)
                    focusedPositionMenu = -1

                    layoutManagerCategory?.findViewByPosition(0)?.let { firstVisibleItem ->
                        firstVisibleItem.setBackgroundResource(R.drawable.hoverb)
                        logHoveredCardData(0)
                        focusedPositionCategory = 0
                    }
                    return true
                } else if (focusedPositionCategory != -1) {
                    layoutManagerCategory?.findViewByPosition(focusedPositionCategory)?.setBackgroundResource(R.drawable.hoverbb)
                    focusedPositionCategory = -1
                    isFirstFocusSet = false
                    return true
                } else if (isCartZoomed) {
                    cartView.scaleX = 1f
                    cartView.scaleY = 1f
                    isFirstFocusSet = false
                    return true
                }
            }

            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (focusedPositionCategory != -1) {
                    val nextPosition = focusedPositionCategory + 1
                    if (nextPosition < (layoutManagerCategory?.itemCount ?: 0)) {
                        val currentVisibleItem = layoutManagerCategory?.findViewByPosition(focusedPositionCategory)
                        val nextVisibleItem = layoutManagerCategory?.findViewByPosition(nextPosition)

                        if (nextVisibleItem == null) {
                            layoutManagerCategory?.scrollToPositionWithOffset(nextPosition, (recyclerViewCategory.width / 2))
                            recyclerViewCategory.post {
                                val updatedVisibleItem = layoutManagerCategory?.findViewByPosition(nextPosition)
                                if (updatedVisibleItem != null) {
                                    logHoveredCardData(nextPosition)
                                    updatedVisibleItem.setBackgroundResource(R.drawable.hoverb)
                                    currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)
                                    focusedPositionCategory = nextPosition
                                }
                            }
                        } else {
                            logHoveredCardData(nextPosition)
                            nextVisibleItem.setBackgroundResource(R.drawable.hoverb)
                            currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)
                            focusedPositionCategory = nextPosition

                            val distanceToCenter = (recyclerViewCategory.width - nextVisibleItem.width) / 2
                            recyclerViewCategory.smoothScrollBy(distanceToCenter / 10, 0)
                        }
                    }
                    return true
                } else if (focusedPositionMenu != -1) {
                    val nextPosition = focusedPositionMenu + 1
                    if (nextPosition < (layoutManagerMenu?.itemCount ?: 0)) {
                        val currentVisibleItem = layoutManagerMenu?.findViewByPosition(focusedPositionMenu)
                        val nextVisibleItem = layoutManagerMenu?.findViewByPosition(nextPosition)

                        if (nextVisibleItem == null) {
                            layoutManagerMenu?.scrollToPositionWithOffset(nextPosition, (recyclerViewMenu.width / 2))
                            recyclerViewMenu.post {
                                val updatedVisibleItem = layoutManagerMenu?.findViewByPosition(nextPosition)
                                if (updatedVisibleItem != null) {
                                    logHoveredCardDataM(nextPosition)
                                    updatedVisibleItem.setBackgroundResource(R.drawable.hoverb)
                                    currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)
                                    focusedPositionMenu = nextPosition
                                }
                            }
                        } else {
                            logHoveredCardDataM(nextPosition)
                            nextVisibleItem.setBackgroundResource(R.drawable.hoverb)
                            currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)
                            focusedPositionMenu = nextPosition

                            val nextItemTop = nextVisibleItem.top
                            val nextItemBottom = nextVisibleItem.bottom
                            val recyclerViewTop = recyclerViewMenu.top
                            val recyclerViewBottom = recyclerViewMenu.bottom

                            if (nextItemTop < recyclerViewTop || nextItemBottom > recyclerViewBottom) {
                                val scrollAmount = nextItemBottom - recyclerViewBottom
                                recyclerViewMenu.smoothScrollBy(0, scrollAmount)
                            }
                        }
                    }
                    return true
                }
            }

            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (focusedPositionCategory != -1) {
                    val previousPosition = focusedPositionCategory - 1
                    if (previousPosition >= 0) {
                        val currentVisibleItem = layoutManagerCategory?.findViewByPosition(focusedPositionCategory)
                        val previousVisibleItem = layoutManagerCategory?.findViewByPosition(previousPosition)

                        if (previousVisibleItem == null) {
                            layoutManagerCategory?.scrollToPositionWithOffset(previousPosition, (recyclerViewCategory.width / 2))
                            recyclerViewCategory.post {
                                val updatedVisibleItem = layoutManagerCategory?.findViewByPosition(previousPosition)
                                if (updatedVisibleItem != null) {
                                    logHoveredCardData(previousPosition)
                                    updatedVisibleItem.setBackgroundResource(R.drawable.hoverb)
                                    currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)
                                    focusedPositionCategory = previousPosition
                                }
                            }
                        } else {
                            logHoveredCardData(previousPosition)
                            previousVisibleItem.setBackgroundResource(R.drawable.hoverb)
                            currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)
                            focusedPositionCategory = previousPosition

                            val distanceToCenter = (recyclerViewCategory.width - previousVisibleItem.width) / 2
                            recyclerViewCategory.smoothScrollBy(-distanceToCenter / 10, 0)
                        }
                    }
                    return true
                } else if (focusedPositionMenu != -1) {
                    val previousPosition = focusedPositionMenu - 1
                    if (previousPosition >= 0) {
                        val currentVisibleItem = layoutManagerMenu?.findViewByPosition(focusedPositionMenu)
                        val previousVisibleItem = layoutManagerMenu?.findViewByPosition(previousPosition)

                        if (previousVisibleItem == null) {
                            layoutManagerMenu?.scrollToPositionWithOffset(previousPosition, (recyclerViewMenu.height / 2))
                            recyclerViewMenu.post {
                                val updatedVisibleItem = layoutManagerMenu?.findViewByPosition(previousPosition)
                                if (updatedVisibleItem != null) {
                                    logHoveredCardDataM(previousPosition)
                                    updatedVisibleItem.setBackgroundResource(R.drawable.hoverb)
                                    currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)
                                    focusedPositionMenu = previousPosition
                                }
                            }
                        } else {
                            logHoveredCardDataM(previousPosition)
                            previousVisibleItem.setBackgroundResource(R.drawable.hoverb)
                            currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)
                            focusedPositionMenu = previousPosition

                            val previousItemTop = previousVisibleItem.top
                            val previousItemBottom = previousVisibleItem.bottom
                            val recyclerViewTop = recyclerViewMenu.top
                            val recyclerViewBottom = recyclerViewMenu.bottom

                            if (previousItemTop < recyclerViewTop || previousItemBottom > recyclerViewBottom) {
                                val scrollAmount = previousItemTop - recyclerViewTop
                                recyclerViewMenu.smoothScrollBy(0, scrollAmount)
                            }
                        }
                    }
                    return true
                }

            }


            KeyEvent.KEYCODE_ENTER -> {

                if (focusedPositionCategory != -1) {
                    printLog("click cat")
                    val categoryData = getCategoryPosition(focusedPositionCategory)
                    categoryData?.let {
                        val menu_type_id = it.menu_type_id
                        val macAddress = getMacAddress() ?: "MAC Address Not Found"

                        printLog("MAC Address: $macAddress, Category ID: $menu_type_id")
//                        Toast.makeText(applicationContext, "MAC Address: $macAddress, Category ID: $menu_type_id", Toast.LENGTH_LONG).show()

                        if (menu_type_id != null) {
                            getDataApi2(menu_type_id)
                            return true
                        }
                    }
                    return true
                } else if (focusedPositionMenu != -1) {
                    val menuData = getMenuPosition(focusedPositionMenu)
                    menuData?.let {
                        val menu_id = it.menu_id
                        printLog("menu id: $menu_id")
//                        val macAddress = getMacAddress() ?: "MAC Address Not Found"

//                        printLog("MAC Address: $macAddress, Category ID: $menu_type_id")
//                        Toast.makeText(applicationContext, "MAC Address: $macAddress, Category ID: $menu_type_id", Toast.LENGTH_LONG).show()

                        if (menu_id != null) {
                            postToCart(menu_id)
                            return true
                        }
                    }
                    return true
                } else if (isCartZoomed) {
//                     printLog("click enter")
                    val intent = Intent(this, OrderFoodPayment::class.java)
                    startActivity(intent)
                }

            }
            }

        return super.onKeyDown(keyCode, event)
    }

    private fun postToCart(menu_id: Int) {

        val macAddress = getMacAddress() ?: ""
        val addToCartFood = AddFoodToCartRequest(macAddress, menu_id)

        printLog("Request: $addToCartFood")

        ApiService.endpoint.addToCartFood(addToCartFood).enqueue(object : Callback<AddFoodToCartRequest> {
            override fun onResponse(call: Call<AddFoodToCartRequest>, response: Response<AddFoodToCartRequest>) {
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
                    Toast.makeText(applicationContext, "You need to complete the payment of previous order first.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@OrderFoodNew, PendingTransaksi::class.java)
                    startActivity(intent)
                    printLog("POST Request Failed: ${response.message()}, Code: ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    printLog("Error Body: $errorBody")
                }
            }


            override fun onFailure(call: Call<AddFoodToCartRequest>, t: Throwable) {
                printLog("API Call Failed: ${t.message}")
            }
        })
    }


        private fun logHoveredCardData(position: Int) {
        val categoryData = getCategoryPosition(position)
        categoryData?.let {
            handleHoveredCard(it)
        }
    }

    private fun logHoveredCardDataM(position: Int) {
        val menuData = getMenuPosition(position)
        menuData?.let {
            handleHoveredCardM(it)
        }
    }

    private fun getMenuPosition(position: Int): DaftarMenuPerCategoryModelItem? {
        val recyclerViewMenu = findViewById<RecyclerView>(R.id.rv_food1)
        val adapter = recyclerViewMenu.adapter as? RecyclerViewDaftarMenuAdapter
        return adapter?.getMenuPosition(position)
    }

    private fun getCategoryPosition(position: Int): MenuType? {
        val recyclerViewCategory = findViewById<RecyclerView>(R.id.rv_food)
        val adapter = recyclerViewCategory.adapter as? RecyclerViewCategoryAdapter
        return adapter?.getCategoryPosition(position)
    }

    private fun handleHoveredCard(categoryData: MenuType) {
        printLog("Hovered Card Data category: $categoryData")
    }

    private fun handleHoveredCardM(menuData: DaftarMenuPerCategoryModelItem) {
        printLog("Hovered Card Data menu: $menuData")
    }

    private fun showCategoryData(categoryData: MenuType) {
        printLog("Selected Category Data: ${categoryData.menu_type_id}")
    }

    private fun showMenuData(menuData: DaftarMenuPerCategoryModelItem) {
        printLog("Selected Menu Data: ${menuData.menu_id}")
    }
}
