package com.example.tvapp

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.tvapp.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FacilityFragment : Fragment() {

    private val TAG: String = "FacilityFragment"
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var facilityAdapter: RecyclerViewFacilityAdapter
    private lateinit var recyclerView: RecyclerView
    private val facilityList: ArrayList<FacilityModelItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
//        facilityList = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_facility, container, false)
        setupRecyclerView(view)
        setupKeyListener(view)
        getDataApi()
        return view
    }



    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.rvfacility)
        facilityAdapter = RecyclerViewFacilityAdapter(facilityList)
        val numberOfColumns = 1
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireContext(), numberOfColumns, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = facilityAdapter
    }

    private var isFirstFocusSet: Boolean = false
    private fun handleHoveredCard(cartData: FacilityModelItem) {
        printLog("Hovered Card Data: $cartData")
    }
    private fun logHoveredCardData(position: Int) {
        if (position >= 0 && position < facilityList.size) {
            val hoveredCardData = facilityList[position]
            printLog("Hovered Card Data: $hoveredCardData")
            handleHoveredCard(hoveredCardData)
        } else {
            printLog("Invalid position or serviceList is empty. Unable to retrieve Hovered Card Data for position: $position")
        }


    }
    private var focusedPosition: Int = 0
    private fun setupKeyListener(view: View) {
        val recyclerViewFacilityList =  view.findViewById<RecyclerView>(R.id.rvfacility)
        val layoutManager = recyclerViewFacilityList.layoutManager as? LinearLayoutManager

        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_DOWN -> {
//                        printLog("HAI")

                        if (!isFirstFocusSet && facilityList.isNotEmpty()) {
                            printLog("HAI")
                            val firstVisibleItem = layoutManager?.findViewByPosition(0)
                            firstVisibleItem?.setBackgroundResource(R.drawable.hoverf)
                            logHoveredCardData(focusedPosition)
                            isFirstFocusSet = true
                            return@setOnKeyListener true
                        }
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        if (isFirstFocusSet) {
                            focusedPosition++
                            val itemCount = layoutManager?.itemCount ?: 0
                            printLog("haii")
                            if (focusedPosition < itemCount) {
                                val nextVisibleItem = layoutManager?.findViewByPosition(focusedPosition)
                                val currentVisibleItem = layoutManager?.findViewByPosition(focusedPosition - 1)
                                if (nextVisibleItem == null) {
                                    layoutManager?.scrollToPositionWithOffset(focusedPosition, (recyclerViewFacilityList.width / 2))
                                    recyclerViewFacilityList.post {
                                        val updatedVisibleItem = layoutManager?.findViewByPosition(focusedPosition)
                                        if (updatedVisibleItem != null) {
                                            logHoveredCardData(focusedPosition)
                                            updatedVisibleItem.setBackgroundResource(R.drawable.hoverf)
                                            currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)
                                        } else {
                                            logHoveredCardData(focusedPosition)
                                            currentVisibleItem?.let {
                                                it.setBackgroundResource(R.drawable.hoverf)
                                                val distanceToCenter = (recyclerViewFacilityList.width - it.width) / 2
                                                recyclerViewFacilityList.smoothScrollBy((distanceToCenter ?: 0) / 10, 0)
                                            }
                                        }
                                    }
                                } else {
                                    logHoveredCardData(focusedPosition)
                                    nextVisibleItem.setBackgroundResource(R.drawable.hoverf)
                                    currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)
                                    val distanceToCenter = (recyclerViewFacilityList.width - nextVisibleItem.width) / 2
                                    recyclerViewFacilityList.smoothScrollBy(distanceToCenter / 10, 0)
                                }
                            } else {
                                val lastItemPosition = focusedPosition - 1
                                val smoothScroller = object : LinearSmoothScroller(recyclerViewFacilityList.context) {
                                    override fun getVerticalSnapPreference(): Int {
                                        return SNAP_TO_START
                                    }
                                }
                                smoothScroller.targetPosition = lastItemPosition
                                layoutManager?.startSmoothScroll(smoothScroller)
                            }
                            return@setOnKeyListener true
                        }

//                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        if (isFirstFocusSet) {
                            if (focusedPosition >= 0) {
                                focusedPosition--
                                val previousVisibleItem = layoutManager?.findViewByPosition(focusedPosition)
                                val currentVisibleItem = layoutManager?.findViewByPosition(focusedPosition + 1)

                                if (previousVisibleItem == null) {
                                    layoutManager?.scrollToPositionWithOffset(focusedPosition, (recyclerViewFacilityList.width / 2))
                                    recyclerViewFacilityList.post {
                                        val updatedVisibleItem = layoutManager?.findViewByPosition(focusedPosition)
                                        if (updatedVisibleItem != null) {
                                            logHoveredCardData(focusedPosition)
                                            updatedVisibleItem.setBackgroundResource(R.drawable.hoverf)
                                            currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)
                                        }
                                    }
                                } else {
                                    logHoveredCardData(focusedPosition)
                                    previousVisibleItem.setBackgroundResource(R.drawable.hoverf)
                                    currentVisibleItem?.setBackgroundResource(R.drawable.hoverbb)
                                    val distanceToCenter = (recyclerViewFacilityList.width - previousVisibleItem.width) / 2
                                    recyclerViewFacilityList.smoothScrollBy(-distanceToCenter / 10, 0)
                                }
                            }
                            return@setOnKeyListener true
                        }
                    }
                }
            }
            // Return false if the event is not handled
            false
        }
    }



    private fun getMacAddress(): String? {
        val wifiManager = requireContext().getSystemService(Context.WIFI_SERVICE) as WifiManager?
        val wifiInfo = wifiManager?.connectionInfo
        return wifiInfo?.macAddress
    }

    private fun getDataApi() {
        val macAddress = getMacAddress()
        if (macAddress != null) {
            printLog("MAC Address: $macAddress")
            ApiService.endpoint.getfacilities(macAddress)
                .enqueue(object : Callback<List<FacilityModelItem>> {
                    override fun onResponse(
                        call: Call<List<FacilityModelItem>>,
                        response: Response<List<FacilityModelItem>>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result != null) {
                                printLog("API Response: $result")
                                facilityAdapter.setData(result)
                            } else {
                                printLog("Response body is null.")
                            }
                        } else {
                            printLog("API Call Failed: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<List<FacilityModelItem>>, t: Throwable) {
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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FacilityFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
