package com.example.tvapp.retrofit

import com.example.tvapp.statusmodel
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object ApiService {
//    val BASE_URL: String = "http://10.218.15.221:8002/api/";
    val BASE_URL: String = "https://iptv-hms.api.dev.mas-ts.com/api/";
//    https://iptv-hms.socket.dev.mas-ts.com/
//    https://8730-103-75-66-1.ngrok-free.app/api/
//    http://localhost:8000/api/
    val URL_MIDTRANS: String = "https://api.sandbox.midtrans.com/v2/"

    val endpoint: ApiEndpoint
        get() {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiEndpoint::class.java)
        }

    val endpointMidtrans: ApiEndpoint
        get() {
            val retrofit = Retrofit.Builder()
                .baseUrl(URL_MIDTRANS)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiEndpoint::class.java)
        }

    fun getStatusMidtrans(orderId: String): Call<statusmodel> {
        return endpointMidtrans.getStatusMID(orderId)
    }
}
