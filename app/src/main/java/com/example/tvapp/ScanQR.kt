package com.example.tvapp

import android.app.Dialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

import java.util.Hashtable

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.tvapp.retrofit.ApiService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanQR(context: Context, private val paymentUrl: String?, private val orderId: String?, private val databaseId: Int) : Dialog(context) {

    private val TAG: String = "QRIS"
    private var isFirstFocusSet: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.activity_scan_qr)

        val closeButton = findViewById<Button>(R.id.close1)
        closeButton.setOnClickListener {
            // Print databaseId to verify it's being passed correctly
            printLog("databaseId: $databaseId")

            // Wrap the single databaseId in a list and call postPaymentStatus
            postPaymentStatus(databaseId)
            dismiss()
        }

        printLog("ORDER ID DI SCAN QR : $orderId")
        printLog("DATABASE ID DI SCAN QR: : $databaseId")

        paymentUrl?.let {
            val qrBitmap = generateQRCode(it)
            findViewById<ImageView>(R.id.wifi).setImageBitmap(qrBitmap)
        }
    }

    private fun generateQRCode(text: String): Bitmap? {
        val hints: Hashtable<EncodeHintType, Any> = Hashtable()
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        val writer = MultiFormatWriter()
        try {
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            return bmp
        } catch (e: WriterException) {
            Log.e(TAG, "generateQRCode: ${e.message}", e)
        }
        return null
    }

    private fun postPaymentStatus(databaseIds: Int) {
        val paymentStatus = PostPaymentStatusModel(databaseId)


        ApiService.endpoint.postPaymentStatus(paymentStatus).enqueue(object : Callback<PostPaymentStatusModel> {
            override fun onResponse(call: Call<PostPaymentStatusModel>, response: Response<PostPaymentStatusModel>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        printLog("POST Response: $responseBody")
                    } else {
                        printLog("POST Response is null.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    printLog("Failed to post payment status: ${response.message()}, Error Body: $errorBody, Response Code: ${response.code()}")
                }
            }


            override fun onFailure(call: Call<PostPaymentStatusModel>, t: Throwable) {
                printLog("API Call Failed: ${t.message}")
            }
        })
    }

    private fun printLog(message: String) {
        Log.d(TAG, message)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    if (!isFirstFocusSet) {
                        val closeButton = findViewById<Button>(R.id.close1)
                        closeButton.scaleX = 1.2f
                        closeButton.scaleY = 1.2f
                        return true
                        isFirstFocusSet = true
                    }

                }

                KeyEvent.KEYCODE_ENTER -> {
                    if (isFirstFocusSet) {
                        postPaymentStatus(databaseId)
                        dismiss()
                        return true
                    }
                }

            }
        }
        return super.dispatchKeyEvent(event)
    }


}
