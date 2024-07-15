package com.example.tvapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.activity_change_payment_method)

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
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val cashRadioButton = findViewById<RadioButton>(R.id.radio_cash)
        val scanQrRadioButton = findViewById<RadioButton>(R.id.radio_scan_qr)

        when {
            paymentMethod.equals("Cash", ignoreCase = true) -> {
                cashRadioButton?.apply {
                    text = "Cash"
                    visibility = RadioButton.VISIBLE
                    isChecked = true
                }
            }
            paymentMethod.equals("Scan QR", ignoreCase = true) -> {
                scanQrRadioButton?.apply {
                    text = "Scan QR"
                    visibility = RadioButton.VISIBLE
                    isChecked = true
                }
            }
            else -> {
                cashRadioButton?.visibility = RadioButton.VISIBLE
                scanQrRadioButton?.visibility = RadioButton.VISIBLE
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
