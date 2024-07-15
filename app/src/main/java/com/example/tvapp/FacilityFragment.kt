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
        facilityAdapter = RecyclerViewFacilityAdapter(ArrayList())
        val numberOfColumns = 1
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireContext(), numberOfColumns, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = facilityAdapter
    }

    private fun setupKeyListener(view: View) {
        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        printLog("HAI")
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_DPAD_UP -> {
                        printLog("haii")
                        return@setOnKeyListener true
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
