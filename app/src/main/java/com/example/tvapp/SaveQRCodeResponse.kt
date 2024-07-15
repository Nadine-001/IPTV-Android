//package com.example.tvapp
//
//data class SaveQRCodeRequest(
//    val food_request_id: Int,
//    val qr_code: String
//)

package com.example.tvapp

data class SaveQRCodeResponse(
        val food_request_id: Int,
    val qr_code: String,
        val qr_code_expire_time: String
)

