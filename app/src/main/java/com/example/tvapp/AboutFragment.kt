package com.example.tvapp

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.wifi.WifiManager
import android.content.Intent
import com.example.tvapp.AboutModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.tvapp.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AboutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AboutFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val TAG: String = "AboutFragment"


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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onStart() {
        super.onStart()
        getDataFromApi()
    }

    private fun getMacAddress(): String? {
        val wifiManager = requireContext().getSystemService(Context.WIFI_SERVICE) as WifiManager?
        val wifiInfo = wifiManager?.connectionInfo
        return wifiInfo?.macAddress
    }

    private fun getDataFromApi() {
        val macAddress = getMacAddress()

        if (macAddress != null) {
            printLog("MAC Address: $macAddress")
        ApiService.endpoint.getAbout(macAddress)
            .enqueue(object : Callback<AboutModel> {
                override fun onResponse(call: Call<AboutModel>, response: Response<AboutModel>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null) {
                            // Find the TextView and RatingBar within the fragment's layout
                            val hotelName = view?.findViewById<TextView>(R.id.helloTextView)
                            val ratingBar = view?.findViewById<RatingBar>(R.id.ratingBar)
//                            val descHotel = view?.findViewById<TextView>(R.id.welcomeTextView)


                            // Set hotel_name to TextView
                            hotelName?.text = result.hotel_name

//                            descHotel?.text = result.hotel_about

                            // Set hotel_class to RatingBar (make sure it's within valid range)
                            ratingBar?.rating = result.hotel_class.toFloat()

                            // Find the TextView within the fragment's layout
                            val cekinTextView = view?.findViewById<TextView>(R.id.cekin)

                            // Format and set hotel_check_out to TextView
                            val checkInTime = SimpleDateFormat("hh:mm a").format(
                                SimpleDateFormat("HH:mm:ss").parse(result.hotel_check_in)
                            )

                            cekinTextView?.text = "Check-out time: $checkInTime"

                            // Find the TextView within the fragment's layout
                            val cekoutTextView = view?.findViewById<TextView>(R.id.cekout)

                            // Format and set hotel_check_out to TextView
                            val checkOutTime = SimpleDateFormat("hh:mm a").format(
                                SimpleDateFormat("HH:mm:ss").parse(result.hotel_check_out)
                            )

                            cekoutTextView?.text = "Check-out time: $checkOutTime"

                            // Display hotel_greeting in TextView
                            val descHotel = view?.findViewById<TextView>(R.id.welcomeTextView)
                            val htmlContent = result.hotel_about
                            val spannedHtmlContent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY)
                            } else {
                                @Suppress("DEPRECATION")
                                Html.fromHtml(htmlContent)
                            }
                            descHotel?.text = spannedHtmlContent

                            val wifiImageView = view?.findViewById<ImageView>(R.id.wifi)

// Load the image into the ImageView using Glide
                            wifiImageView?.let {
                                Glide.with(requireContext())
                                    .load(result.hotel_photo)
                                    .into(it)
                            }


                            printLog("API Response: $result")
                        }
                        printLog(result.toString())
                    }
                }

                override fun onFailure(call: Call<AboutModel>, t: Throwable) {
                    printLog(t.toString())
                }
            })}
    }


    private fun printLog(message: String) {
        Log.d(TAG, message)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AboutFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AboutFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}