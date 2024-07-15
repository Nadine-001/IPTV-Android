package com.example.tvapp

import com.google.gson.Gson

fun Any.toJson(): String = Gson().toJson(this)

data class SaveQRCodeRequest(
    val food_request_id: Int,
    val qr_code: String
)

//package com.example.tvapp
//
//data class SaveQRCodeRequest(
//        val food_request_id: Int,
//    val qr_code: String,
//        val qr_code_expire_time: String
//)

