package com.example.tvapp

import android.content.Context
import android.net.Uri
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import android.widget.VideoView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.tvapp.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Handler

class ListChannelTV : AppCompatActivity() {
    private val TAG: String = "CHANNEL TV LIST"
    private var isFirstFocusSet: Boolean = false // Flag untuk menandai apakah fokus pertama sudah diatur
    private var focusedPosition: Int = 0
    private var recyclerView: RecyclerView? = null
    private var recyclerViewChannelAdapter: RecyclerViewChannelAdapter? = null
    private var channelList: ArrayList<ChannelModelItem> = ArrayList()
    private lateinit var videoView: VideoView
    private var currentChannelIndex = 0
    private lateinit var bgRelativeLayout: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_channel_tv)
        supportActionBar?.hide()
        bgRelativeLayout = findViewById(R.id.bg)

        val handler = Handler()
        handler.postDelayed({
            bgRelativeLayout.visibility = View.GONE
        }, 5000)

        channelList = ArrayList()
        recyclerView = findViewById<View>(R.id.rv_channel) as RecyclerView
        videoView = findViewById(R.id.videoView)

        recyclerViewChannelAdapter = RecyclerViewChannelAdapter(ArrayList(), object : RecyclerViewChannelAdapter.OnAdapterListener {
            override fun onClick(channel: ChannelModelItem) {
                Toast.makeText(applicationContext, channel.id, Toast.LENGTH_SHORT).show()

                currentChannelIndex = channelList.indexOf(channel)
                playVideo(channel.url)
            }
        })

        val numberOfColumns = 1
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, numberOfColumns, LinearLayoutManager.HORIZONTAL, false)

        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = recyclerViewChannelAdapter
    }

    override fun onStart() {
        super.onStart()
        getDataApi()
    }

    private fun getMacAddress(): String? {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        val wifiInfo = wifiManager?.connectionInfo
        return wifiInfo?.macAddress
    }

    private fun getDataApi() {
        val macAddress = getMacAddress()
        if (macAddress != null) {
            printLog("MAC Address: $macAddress")
            ApiService.endpoint.getChannel(macAddress)
                .enqueue(object : Callback<List<ChannelModelItem>> {
                    override fun onResponse(
                        call: Call<List<ChannelModelItem>>,
                        response: Response<List<ChannelModelItem>>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result != null) {
                                printLog("API Response: $result")
                                channelList.clear()
                                channelList.addAll(result)
                                recyclerViewChannelAdapter?.setData(result)

                                if (channelList.isNotEmpty()) {
                                    currentChannelIndex = 0
                                    playVideo(channelList[currentChannelIndex].url)
                                }
                            } else {
                                printLog("Response body is null.")
                            }
                        } else {
                            printLog("API Call Failed: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<List<ChannelModelItem>>, t: Throwable) {
                        printLog("API Call Failed: ${t.message}")
                    }
                })
        } else {
            printLog("Unable to obtain MAC address.")
        }
    }

    private fun playVideo(videoUrl: String) {
        val videoUri = Uri.parse(videoUrl)
        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener { mp ->
            mp.start()
        }
        videoView.setOnErrorListener { mp, what, extra ->
            if (currentChannelIndex < channelList.size - 1) {
                currentChannelIndex++
                Toast.makeText(this, "Automatically play another TV channel, because the TV channel you selected is not available.", Toast.LENGTH_SHORT).show()

                playVideo(channelList[currentChannelIndex].url)
            } else {
                Toast.makeText(this, "All channels failed to play.", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private fun printLog(message: String) {
        Log.d(TAG, message)
    }

    private fun logHoveredCardData(position: Int) {
        val channelData = getChannelDataAtPosition(position)
        channelData?.let {
            handleHoveredCard(it)
        }
    }

    private fun handleHoveredCard(channelData: ChannelModelItem) {
        printLog("Hovered Card Data: $channelData")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val recyclerViewChannelList = findViewById<RecyclerView>(R.id.rv_channel)
        val layoutManager = recyclerViewChannelList.layoutManager as? LinearLayoutManager

        if (layoutManager == null) {
            printLog("LayoutManager is null")
            return super.onKeyDown(keyCode, event)
        }

        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_DOWN -> {

                if (!isFirstFocusSet) {
                    bgRelativeLayout.visibility = View.VISIBLE
                    val handler = Handler()
                    handler.postDelayed({
                        bgRelativeLayout.visibility = View.GONE
                    }, 5000)

                    val firstVisibleItem = layoutManager.findViewByPosition(0)
                    firstVisibleItem?.setBackgroundResource(R.drawable.hoverb)
                    logHoveredCardData(focusedPosition)
                    isFirstFocusSet = true
                    return true
                } else {
                    if (focusedPosition < layoutManager.itemCount) {
                        bgRelativeLayout.visibility = View.VISIBLE
                        val handler = Handler()
                        handler.postDelayed({
                            bgRelativeLayout.visibility = View.GONE
                        }, 5000)
                        val lastVisibleItem = layoutManager.findViewByPosition(focusedPosition)
                        lastVisibleItem?.setBackgroundResource(R.drawable.hoverb)
                    }
                }
            }



            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (isFirstFocusSet) {
                    focusedPosition++
                    val handler = Handler()
                    handler.postDelayed({
                        bgRelativeLayout.visibility = View.GONE
                    }, 20000)
                    val itemCount = layoutManager.itemCount

                    if (focusedPosition < itemCount) {
                        val nextVisibleItem = layoutManager.findViewByPosition(focusedPosition)
                        val currentVisibleItem = layoutManager.findViewByPosition(focusedPosition - 1)

                        if (nextVisibleItem == null) {
                            layoutManager.scrollToPositionWithOffset(focusedPosition, (recyclerViewChannelList.width / 2))
                            recyclerViewChannelList.post {
                                val updatedVisibleItem = layoutManager.findViewByPosition(focusedPosition)
                                if (updatedVisibleItem != null) {
                                    logHoveredCardData(focusedPosition)
                                    updatedVisibleItem.setBackgroundResource(R.drawable.hoverb)
                                    currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)
                                }
                            }
                        } else {
                            logHoveredCardData(focusedPosition)
                            nextVisibleItem.setBackgroundResource(R.drawable.hoverb)
                            currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)

                            val distanceToCenter = (recyclerViewChannelList.width - nextVisibleItem.width) / 2
                            recyclerViewChannelList.smoothScrollBy(distanceToCenter / 10, 0)
                        }
                    } else {
                        val lastItemPosition = focusedPosition - 1
                        val smoothScroller = object : LinearSmoothScroller(recyclerViewChannelList.context) {
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
                if (isFirstFocusSet) {
                    if (focusedPosition > 0) {
                        focusedPosition--
                        val handler = Handler()
                        handler.postDelayed({
                            bgRelativeLayout.visibility = View.GONE
                        }, 20000)
                        val itemCount = layoutManager.itemCount

                        val previousVisibleItem = layoutManager.findViewByPosition(focusedPosition)
                        val currentVisibleItem = layoutManager.findViewByPosition(focusedPosition + 1)

                        if (previousVisibleItem == null) {
                            layoutManager.scrollToPositionWithOffset(focusedPosition, (recyclerViewChannelList.width / 2))
                            recyclerViewChannelList.post {
                                val updatedVisibleItem = layoutManager.findViewByPosition(focusedPosition)
                                if (updatedVisibleItem != null) {
                                    logHoveredCardData(focusedPosition)
                                    updatedVisibleItem.setBackgroundResource(R.drawable.hoverb)
                                    currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)
                                }
                            }
                        } else {
                            logHoveredCardData(focusedPosition)
                            previousVisibleItem.setBackgroundResource(R.drawable.hoverb)
                            currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)

                            val distanceToCenter = (recyclerViewChannelList.width - previousVisibleItem.width) / 2
                            recyclerViewChannelList.smoothScrollBy(-distanceToCenter / 10, 0)
                        }
                    }

                    return true
                }
            }

            KeyEvent.KEYCODE_ENTER -> {
                if (isFirstFocusSet) {
                    val channelData = getChannelDataAtPosition(focusedPosition)
                    channelData?.let {
                        val url = it.url
                        printLog("URLNYA: $url")
                        playVideo(url)

                        // Tunggu 3 detik sebelum menyembunyikan RelativeLayout
                        val handler = Handler()
                        handler.postDelayed({
                            bgRelativeLayout.visibility = View.GONE
                        }, 5000)
                    }
                    return true
                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }
    private fun getChannelDataAtPosition(position: Int): ChannelModelItem? {
        val recyclerViewChannelList = findViewById<RecyclerView>(R.id.rv_channel)
        val adapter = recyclerViewChannelList.adapter as? RecyclerViewChannelAdapter
        return adapter?.getChannelDataAtPosition(position)
    }

}