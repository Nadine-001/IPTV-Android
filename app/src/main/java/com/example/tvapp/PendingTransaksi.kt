package com.example.tvapp

import android.content.ContentValues.TAG
import android.content.Context
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tvapp.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import java.text.NumberFormat
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent

class PendingTransaksi : AppCompatActivity(){
    private val TAG: String = "pending"
    private lateinit var pendingTransaksiModel: PendingTransaksiModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewPendingAdapter: RecyclerViewPendingAdapter
    private var pendingList: ArrayList<UnpaidOrder> = ArrayList()
    private var databaseId: Int = -1
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_transaksi)
        supportActionBar?.hide()

        pendingList = ArrayList()

        recyclerView = findViewById<View>(R.id.rv_pending1) as RecyclerView

        val numberOfColumns = 1

        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, numberOfColumns, LinearLayoutManager.VERTICAL, false)

        recyclerView!!.layoutManager = layoutManager
        // Inisialisasi adapter dengan data kosong
        recyclerViewPendingAdapter = RecyclerViewPendingAdapter(pendingList)
        recyclerView.adapter = recyclerViewPendingAdapter

        // Tambahkan listener untuk welcomeButton2
        val welcomeButton2 = findViewById<Button>(R.id.welcomeButton2)
        welcomeButton2.setOnClickListener {
            getQR()
        }

        handler = Handler()
        runnable = Runnable {
            getData()
            handler.postDelayed(runnable, 1000) // Perbarui setiap detik (1000 ms)
        }
        startHandler()
    }
    private var isFirstFocusSet: Boolean = false
    private var isSecondFocusSet: Boolean = false

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_DOWN -> {

                    if (!isFirstFocusSet) {
                        val welcomeButton2 = findViewById<Button>(R.id.welcomeButton2)
                        welcomeButton2.scaleX = 1.2f
                        welcomeButton2.scaleY = 1.2f

                        isFirstFocusSet = true
                    }
return true

                }

                KeyEvent.KEYCODE_ENTER -> {
                    if (isFirstFocusSet) {
                        getQR()

                        return true
                    }

                }

            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)
    }

    private fun startHandler() {
        handler.post(runnable)
    }

    private fun getMacAddress(): String? {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        val wifiInfo = wifiManager?.connectionInfo
        return wifiInfo?.macAddress
    }

    private fun getData() {
        val macAddress = getMacAddress()
        if (macAddress != null) {
            ApiService.endpoint.getPending(macAddress)
                .enqueue(object : Callback<PendingTransaksiModel> {
                    override fun onResponse(
                        call: Call<PendingTransaksiModel>,
                        response: Response<PendingTransaksiModel>
                    ) {
                        val result = response.body()
                        if (result != null) {
                            pendingTransaksiModel = result
                            pendingList.clear()
                            pendingList.addAll(result.unpaid_order)
                            recyclerViewPendingAdapter.setData(result.unpaid_order)
                            val total = findViewById<TextView>(R.id.total1)

                            val formattedTotal = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(result.total)

                            total.text = formattedTotal
                            databaseId = result.food_request_id
                        }
                        printLog(result.toString())
                    }

                    override fun onFailure(call: Call<PendingTransaksiModel>, t: Throwable) {
                        printLog("API Call Failed: ${t.message}")
                    }

                })
        }

    }

    private fun getQR() {
        if (databaseId != -1) {
            ApiService.endpoint.getShowQRAgain(databaseId)
                .enqueue(object : Callback<ModelShowQRAgain> {
                    override fun onResponse(
                        call: Call<ModelShowQRAgain>,
                        response: Response<ModelShowQRAgain>
                    ) {
                        val result = response.body()
                        if (result != null) {
                            printLog("Database ID: $databaseId")

                            val dialog = ScanQRAgain(this@PendingTransaksi, databaseId)
                            dialog.show()

                            getData()

                        }
                        printLog(result.toString())
                    }

                    override fun onFailure(call: Call<ModelShowQRAgain>, t: Throwable) {
                        printLog("API Call Failed: ${t.message}")
                    }

                })

        }
    }

    private fun printLog(message: String) {
        Log.d(TAG, message)
    }
}