package com.example.tvapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.tvapp.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePaymentMethod(context: Context, private val databaseId: Int) : Dialog(context) {
    private val TAG: String = "Change Payment"
    private lateinit var cashRadioButton: RadioButton
    private lateinit var scanQrRadioButton: RadioButton
    private var currentPaymentMethod: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.activity_change_payment_method)

        // Initialize the radio buttons here
        cashRadioButton = findViewById(R.id.radio_cash)
        scanQrRadioButton = findViewById(R.id.radio_scan_qr)

        getPaymentMethod()

        val closeButton = findViewById<Button>(R.id.close1)
        closeButton.setOnClickListener {
            dismiss()
        }

        val putButton = findViewById<Button>(R.id.close2)
        putButton.setOnClickListener {
            putPayment()
        }
    }
    private var isRadioHovered = false
    private var changeButton = false
    private var closeButton = false
    private var cashRadioHovered = false

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        Log.d(TAG, "Key Event: ${event?.keyCode}")
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                     if (cashRadioHovered) {
                        // Remove hover from cashRadioButton, hover scanQrRadioButton
//                        cashRadioButton.isHovered = false
                        cashRadioHovered = false
                        scanQrRadioButton.setBackgroundResource(R.drawable.hover)
                        cashRadioButton.setBackgroundResource(0)

                        isRadioHovered = true
                        return true
                    }
                    if (!isRadioHovered && !changeButton && !closeButton) {
                        // Hover scanQrRadioButton
                        scanQrRadioButton.setBackgroundResource(R.drawable.hover)
                        isRadioHovered = true
                        return true
                    }
                    else if (isRadioHovered) {
                        // Zoom close2
                        findViewById<Button>(R.id.close2)?.apply {
                            scaleX = 1.2f
                            scaleY = 1.2f
                        }
                        scanQrRadioButton.setBackgroundResource(0)
                        isRadioHovered = false
                        changeButton = true
                        return true
                    } else if (changeButton && !closeButton) {
                        // Zoom close1, reset close2
                        findViewById<Button>(R.id.close1)?.apply {
                            scaleX = 1.2f
                            scaleY = 1.2f
                        }
                        findViewById<Button>(R.id.close2)?.apply {
                            scaleX = 1.0f
                            scaleY = 1.0f
                        }
                        changeButton = false
                        closeButton = true
                        return true
                    } else if (closeButton) {
                        // Reset close1, hover scanQrRadioButton
                        findViewById<Button>(R.id.close1)?.apply {
                            scaleX = 1.0f
                            scaleY = 1.0f
                        }
                        scanQrRadioButton.setBackgroundResource(R.drawable.hover)
                        closeButton = false
                        isRadioHovered = true
                        return true
                    }
                }
                KeyEvent.KEYCODE_DPAD_UP -> {
                    if (isRadioHovered) {
                        // Hover cashRadioButton
                        cashRadioButton.setBackgroundResource(R.drawable.hover)
                        scanQrRadioButton.setBackgroundResource(0)
                        isRadioHovered = false
                        cashRadioHovered = true
                        return true
                    } else if (!isRadioHovered && !changeButton && !closeButton) {
                        // Hover scanQrRadioButton
                        scanQrRadioButton.setBackgroundResource(R.drawable.hover)
                        cashRadioButton.setBackgroundResource(0)
                        isRadioHovered = true
                        cashRadioHovered = false
                        return true
                    } else if (changeButton) {
                        // Reset close2, hover scanQrRadioButton
                        findViewById<Button>(R.id.close2)?.apply {
                            scaleX = 1.0f
                            scaleY = 1.0f
                        }
                        scanQrRadioButton.setBackgroundResource(R.drawable.hover)
                        changeButton = false
                        isRadioHovered = true
                        return true
                    } else if (closeButton) {
                        // Zoom close2, reset close1
                        findViewById<Button>(R.id.close2)?.apply {
                            scaleX = 1.2f
                            scaleY = 1.2f
                        }
                        findViewById<Button>(R.id.close1)?.apply {
                            scaleX = 1.0f
                            scaleY = 1.0f
                        }
                        closeButton = false
                        changeButton = true
                        return true
                    }
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, event)
                    return true
                }
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, event)
                    return true
                }
                KeyEvent.KEYCODE_ENTER -> {
                    if (cashRadioButton.isHovered) {
                        // Panggil updateRadioButtons untuk cashRadioButton
                        updateRadioButtons("Cash")
                        return true
                    } else if (isRadioHovered) {
                        // Panggil updateRadioButtons untuk scanQrRadioButton
//                        Log.d(TAG, "scanQrRadioButton is hovered")
                        updateRadioButtons("Scan QR")
                        return true
                    } else if (closeButton) {
                        // Panggil updateRadioButtons untuk scanQrRadioButton
//                        Log.d(TAG, "scanQrRadioButton is hovered")
                        dismiss()
                        return true
                    }
                    else if (changeButton) {
                        // Panggil updateRadioButtons untuk scanQrRadioButton
//                        Log.d(TAG, "scanQrRadioButton is hovered")
                        putPayment()
                        return true
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }









    private fun getPaymentMethod() {
        ApiService.endpoint.getPaymentMethod(databaseId)
            .enqueue(object : Callback<ModelPaymentMethod> {
                override fun onResponse(
                    call: Call<ModelPaymentMethod>,
                    response: Response<ModelPaymentMethod>
                ) {
                    if (response.isSuccessful) {
                        val paymentData = response.body()
                        if (paymentData != null) {
                            val paymentMethod = paymentData.payment_method
                            printLog("PAYMENT METHOD: $paymentMethod")
                            updateRadioButtons(paymentMethod)
                        } else {
                            printLog("Response body is null.")
                        }
                    } else {
                        val errorMessage = response.errorBody()?.string()
                        printLog("API call failed: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<ModelPaymentMethod>, t: Throwable) {
                    printLog("Failed to fetch payment method data: ${t.message}")
                }
            })
    }

    private fun updateRadioButtons(paymentMethod: String) {
        currentPaymentMethod = paymentMethod

        when {
            paymentMethod.equals("Cash", ignoreCase = true) -> {
                cashRadioButton.apply {
                    text = "Cash"
                    visibility = RadioButton.VISIBLE
                    isChecked = true
                }
            }
            paymentMethod.equals("Scan QR", ignoreCase = true) -> {
                scanQrRadioButton.apply {
                    text = "Scan QR"
                    visibility = RadioButton.VISIBLE
                    isChecked = true
                }
            }
            else -> {
                cashRadioButton.visibility = RadioButton.VISIBLE
                scanQrRadioButton.visibility = RadioButton.VISIBLE
            }
        }
    }

    private fun putPayment() {
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val selectedRadioButtonId = radioGroup.checkedRadioButtonId
        val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)

        if (selectedRadioButton != null) {
            val newPaymentMethod = selectedRadioButton.text.toString()
            val putPaymentModel = PutPaymentMethodModel(databaseId, newPaymentMethod)

            ApiService.endpoint.putPayment(putPaymentModel)
                .enqueue(object : Callback<PutPaymentMethodModel> {
                    override fun onResponse(
                        call: Call<PutPaymentMethodModel>,
                        response: Response<PutPaymentMethodModel>
                    ) {
                        if (response.isSuccessful) {
                            printLog("Payment method updated successfully: $newPaymentMethod")
                            dismiss()
                            navigateToOrderFoodNew()
                        } else {
                            val errorMessage = response.errorBody()?.string()
                            printLog("Failed to update payment method: $errorMessage")
                        }
                    }

                    override fun onFailure(call: Call<PutPaymentMethodModel>, t: Throwable) {
                        printLog("Failed to update payment method: ${t.message}")
                    }
                })
        }
    }

    private fun printLog(message: String) {
        Log.d(TAG, message)
    }

    private fun navigateToOrderFoodNew() {
        val intent = Intent(context, OrderFoodNew::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}
