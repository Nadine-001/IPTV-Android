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
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.example.tvapp.retrofit.ApiService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanQRAgain(context: Context, private val databaseId: Int) : Dialog(context) {
    private val TAG: String = "QRIS Again"
    private var isFirstFocusSet: Boolean = false
    private var isSecondFocusSet: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.activity_scan_qragain)

        printLog("Received databaseId: $databaseId")

        val closeButton = findViewById<Button>(R.id.close2)
        closeButton.setOnClickListener {
            dismiss()
        }

        val changeButton = findViewById<Button>(R.id.close1)
        changeButton.setOnClickListener {
            showPaymentMethodPopup()        }

        getQRCodeData()
    }

    private fun showPaymentMethodPopup() {
        val dialog = ChangePaymentMethod(context, databaseId)
        dialog.show()
    }


    private fun getQRCodeData() {
        ApiService.endpoint.getShowQRAgain(databaseId)
            .enqueue(object : Callback<ModelShowQRAgain> {
                override fun onResponse(
                    call: Call<ModelShowQRAgain>,
                    response: Response<ModelShowQRAgain>
                ) {
                    if (response.isSuccessful) {
                        val qrData = response.body()
                        if (qrData != null) {
                            val qrCode = qrData.payment_link
                            printLog("QR Code: $qrCode")
                            generateAndShowQRCode(qrCode)
                        } else {
                            printLog("Response body is null.")
                        }
                    } else {
                        val errorMessage = response.errorBody()?.string()
                        printLog("API call failed: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<ModelShowQRAgain>, t: Throwable) {
                    printLog("Failed to fetch QR code data: ${t.message}")
                }
            })
    }

    private fun generateAndShowQRCode(qrCode: String) {
        val qrBitmap = generateQRCode(qrCode)
        if (qrBitmap != null) {
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

                        isFirstFocusSet = true
                    }   else  {
                        val closeButton = findViewById<Button>(R.id.close1)
                        val closeButton2 = findViewById<Button>(R.id.close2)
                        closeButton2.scaleX = 1.2f
                        closeButton2.scaleY = 1.2f
                        closeButton.scaleX = 1.0f
                        closeButton.scaleY = 1.0f
                        isSecondFocusSet = true
                    }


                }

                KeyEvent.KEYCODE_ENTER -> {
                    if (isSecondFocusSet) {
                        dismiss()

                        return true
                    }
                    if (isFirstFocusSet) {
                        showPaymentMethodPopup()

                        return true
                    }

                }

            }
        }
        return super.dispatchKeyEvent(event)
    }
}
