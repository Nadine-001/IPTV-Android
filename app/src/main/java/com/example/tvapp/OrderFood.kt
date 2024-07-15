//package com.example.tvapp
//
//import android.content.Context
//import android.content.Intent
//import android.net.wifi.WifiManager
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.text.Html
//import android.util.Log
//import android.view.KeyEvent
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.core.content.ContentProviderCompat.requireContext
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.tvapp.retrofit.ApiService
//import org.jsoup.Jsoup
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.security.Key
//import java.text.DateFormat
//import java.text.SimpleDateFormat
//import java.util.Calendar
//import java.util.Locale
//import java.util.TimeZone
//import com.example.tvapp.RecyclerViewMenuAdapter
//import com.example.tvapp.RecyclerViewMenuAdapter.OnItemClickListener
//
//class OrderFood : AppCompatActivity(), RecyclerViewMenuListAdapter.OnItemClickListener {
//    private val TAG: String = "OrderFood"
//    private var menuData: List<MenuModelApiItem>? = null
//    private var isFirstFocusSet = false
//    private var recyclerView: RecyclerView? = null
//    private var recyclerViewMenuAdapter: RecyclerViewMenuAdapter? = null
//    private var menuList: ArrayList<MenuModelApiItem> = ArrayList()
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_order_food)
//        supportActionBar?.hide()
//
//        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
//        val dateFormat = DateFormat.getDateInstance().format(calendar.time)
//
//        val dateTextView = findViewById<TextView>(R.id.text_date)
//        dateTextView.text = dateFormat
//
//        val dayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
//        val dayTextView = findViewById<TextView>(R.id.text_day)
//        val dayOfWeek = dayFormat.format(calendar.time)
//        dayTextView.text = dayOfWeek
//
//        menuList = ArrayList()
//
//        recyclerView = findViewById<View>(R.id.rv_food) as RecyclerView
//        recyclerViewMenuAdapter = RecyclerViewMenuAdapter(ArrayList())
//        recyclerViewMenuAdapter?.setOnItemClickListener(this)
//
//
//        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 1)
//
//        recyclerView!!.layoutManager = layoutManager
//        recyclerView!!.adapter = recyclerViewMenuAdapter
//
//        getDataApi()
//
//        val cartButton = findViewById<ImageView>(R.id.cartIcon)
//        cartButton.setOnClickListener {
//            val intent = Intent(this, OrderFoodPayment::class.java)
//            startActivity(intent)
//
//        }
//
//
//    }
//
//
//    override fun onItemClick(position: Int) {
//        Log.d(TAG, "Clicked item at position: $position")
//    }
//    override fun onStart() {
//        super.onStart()
//        getDataFromApi()
//        getRoomHeader()
//    }
//
//    private var focusedPosition = 0
//
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        val recyclerViewFoodList = findViewById<RecyclerView>(R.id.rv_food_list)
//        val layoutManager = recyclerViewFoodList.layoutManager as LinearLayoutManager
//
//        when (keyCode) {
//            KeyEvent.KEYCODE_DPAD_DOWN -> {
//                if (!isFirstFocusSet) {
//                    val firstVisibleItem = layoutManager.findViewByPosition(0)
//
//                    firstVisibleItem?.elevation = 10f
//
//                    addLeftMarginToLeftElements(firstVisibleItem)
//
//                    firstVisibleItem?.animate()?.scaleX(1.3f)?.scaleY(1.3f)
//                    isFirstFocusSet = true
//                    Log.d(TAG, "Focused position: $focusedPosition")
//                    return true
//                }
//            }
//             KeyEvent.KEYCODE_ENTER->{
//                if (isFirstFocusSet) {
//                    val focusedItem = menuData?.getOrNull(focusedPosition) // Ambil data dari menuData sesuai dengan posisi yang difokuskan
//                    focusedItem?.let {
//                        val toastMessage = "Menu ID: ${it.menu.firstOrNull()?.menu_id}, Menu Name: ${it.menu.firstOrNull()?.menu_name}" // Ambil data menu_name dan menu_id dari item yang difokuskan
//                        Toast.makeText(this@OrderFood, toastMessage, Toast.LENGTH_SHORT).show()
//
//                    }
//                }
//            }
//            KeyEvent.KEYCODE_DPAD_RIGHT -> {
//                if (isFirstFocusSet) {
//                    focusedPosition++
//                    val itemCount = layoutManager.itemCount
//
//                    if (focusedPosition < itemCount) {
//                        val nextVisibleItem = layoutManager.findViewByPosition(focusedPosition)
//                        val currentVisibleItem = layoutManager.findViewByPosition(focusedPosition - 1)
//
//                        nextVisibleItem?.elevation = 20f
//                        currentVisibleItem?.animate()?.scaleX(1.0f)?.scaleY(1.0f)
//                        nextVisibleItem?.animate()?.scaleX(1.3f)?.scaleY(1.3f)
//
//                        val distanceToCenter = (recyclerViewFoodList.width - nextVisibleItem?.width!!) / 2
//                        recyclerViewFoodList.smoothScrollBy(distanceToCenter, -1)
//                    } else {
//                        recyclerViewFoodList.smoothScrollToPosition(focusedPosition - 1)
//                    }
//
//                    Log.d(TAG, "Focused position: $focusedPosition")
//
//                    return true
//                }
//            }
//
//            KeyEvent.KEYCODE_DPAD_LEFT -> {
//                if (isFirstFocusSet) {
//                    focusedPosition--
//
//                    if (focusedPosition >= 0) {
//                        val prevVisibleItem = layoutManager.findViewByPosition(focusedPosition)
//                        val currentVisibleItem = layoutManager.findViewByPosition(focusedPosition + 1)
//
//                        prevVisibleItem?.elevation = 30f
//                        currentVisibleItem?.animate()?.scaleX(1.0f)?.scaleY(1.0f)
//                        prevVisibleItem?.animate()?.scaleX(1.3f)?.scaleY(1.3f)
//
//                        val distanceToCenter = (recyclerViewFoodList.width - prevVisibleItem?.width!!) / 2
//                        recyclerViewFoodList.smoothScrollBy(-distanceToCenter, -1)
//                        Log.d(TAG, "Focused position: $focusedPosition")
//
//                        return true
//
//                    } else {
//                        recyclerViewFoodList.smoothScrollToPosition(focusedPosition)
//                        Log.d(TAG, "Focused position: $focusedPosition")
//
//                        return true
//                    }
//                }
//            }
//
//
//        }
//        return super.onKeyDown(keyCode, event)
//    }
//
//    private fun addLeftMarginToLeftElements(zoomedCard: View?) {
//        val elementsOnLeft = arrayOf(R.id.menu_name, R.id.menu_description, R.id.menu_price)
//        for (elementId in elementsOnLeft) {
//            val element = zoomedCard?.findViewById<View>(elementId)
//            val layoutParams = element?.layoutParams as? ViewGroup.MarginLayoutParams
//            layoutParams?.leftMargin = resources.getDimensionPixelSize(R.dimen.left_margin)
//            element?.layoutParams = layoutParams
//        }
//    }
//
//    private fun resetElevationAndMargin(view: View?) {
//        view?.elevation = 0f
//        val elementsOnLeft = arrayOf(R.id.menu_name, R.id.menu_description, R.id.menu_price)
//        for (elementId in elementsOnLeft) {
//            val element = view?.findViewById<View>(elementId)
//            val layoutParams = element?.layoutParams as? ViewGroup.MarginLayoutParams
//            layoutParams?.leftMargin = 0
//            element?.layoutParams = layoutParams
//        }
//    }
//
//    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
//        when (keyCode) {
//            KeyEvent.KEYCODE_DPAD_UP -> {
//                isFirstFocusSet = false
//            }
//        }
//        return super.onKeyUp(keyCode, event)
//    }
//
//    private fun getMacAddress(): String? {
//        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
//        val wifiInfo = wifiManager?.connectionInfo
//        return wifiInfo?.macAddress
//    }
//
//    private fun getRoomHeader(){
//        val macAddress = getMacAddress()
//        if (macAddress != null) {
//            ApiService.endpoint.getHeader(macAddress)
//                .enqueue(object : Callback<RoomHeaderModel> {
//                    override fun onResponse(
//                        call: Call<RoomHeaderModel>,
//                        response: Response<RoomHeaderModel>
//                    ) {
//                        val result = response.body()
//                        if (result != null) {
//                            val guestName = findViewById<TextView>(R.id.guest_name)
//                            guestName.text = result.guest_name
//                        }
//                        printLog(result.toString())
//                    }
//
//                    override fun onFailure(call: Call<RoomHeaderModel>, t: Throwable) {
//                        printLog("API Call Failed: ${t.message}")
//                    }
//
//                })
//        }
//
//    }
//
//    private fun getDataFromApi() {
//        val macAddress = getMacAddress()
//
//        if (macAddress != null) {
//            printLog("MAC Address: $macAddress")
//            ApiService.endpoint.getHeaderFood(macAddress)
//                .enqueue(object : Callback<AdsModel> {
//                    override fun onResponse(
//                        call: Call<AdsModel>,
//                        response: Response<AdsModel>
//                    ) {
//                        if (response.isSuccessful) {
//                            val result = response.body()
//                            if (result != null) {
//
//                                val orderFoodIntro = findViewById<TextView>(R.id.order_food_intro)
//                                val htmlContent = result.order_food_intro
//                                val spannedHtmlContent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                                    Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY)
//                                } else {
//                                    @Suppress("DEPRECATION")
//                                    Html.fromHtml(htmlContent)
//                                }
//                                orderFoodIntro.text = spannedHtmlContent
////                                val orderFoodIntro = findViewById<TextView>(R.id.order_food_intro)
////                                val htmlContent = result.order_food_intro
////
////                                val document = Jsoup.parse(htmlContent)
////                                val paragraphContent = document.select("p").text()
////
////                                orderFoodIntro.text = paragraphContent
//                                printLog("API Response: $result")
//                            } else {
//                                printLog("Response body is null.")
//                            }
//                            printLog(result.toString())
//                        }
//                    }
//
//                    override fun onFailure(call: Call<AdsModel>, t: Throwable) {
//                        printLog((t.toString()))
//                    }
//                })
//        } else {
//            printLog("Unable to obtain MAC address.")
//        }
//    }
//
//    private fun printLog(message: String) {
//        Log.d(TAG, message)
//    }
//
//    private fun getDataApi(){
//        val macAddress = getMacAddress()
//        if (macAddress != null) {
//            printLog("MAC Address: $macAddress")
//            ApiService.endpoint.getMenuList(macAddress)
//                .enqueue(object : Callback<List<MenuModelApiItem>> {
//                    override fun onResponse(
//                        call: Call<List<MenuModelApiItem>>,
//                        response: Response<List<MenuModelApiItem>>
//                    ) {
//                        if (response.isSuccessful) {
//                            val result = response.body()
//                            if (result != null) {
//                                printLog("API Response: $result")
//                                recyclerViewMenuAdapter?.setData(result)                            } else {
//                                printLog("Response body is null.")
//                            }
//                        } else {
//                            printLog("API Call Failed: ${response.message()}")
//                        }
//                    }
//
//                    override fun onFailure(call: Call<List<MenuModelApiItem>>, t: Throwable) {
//                        printLog((t.toString()))
//                    }
//                })}
//    }
//}