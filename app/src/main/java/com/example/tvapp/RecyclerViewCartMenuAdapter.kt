package com.example.tvapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton

class RecyclerViewCartMenuAdapter(private val cartList : MutableList<OrderX>,
//                                  private val listener : OnAdapterListener
) :
    RecyclerView.Adapter<RecyclerViewCartMenuAdapter.MyViewHolder>(){
    private var selectedItemId: Int? = null

    fun getCartPosition(position: Int): OrderX? {
        return if (position in 0 until cartList.size) {
            cartList[position]
        } else {
            null
        }
    }

    fun setSelectedItemId(itemId: Int?) {
        selectedItemId = itemId
        notifyDataSetChanged()
    }

    private var totalPrice: Int = 0

//    fun updateTotalPrice() {
//        // Hitung total harga berdasarkan nilai quantity yang diperbarui
//        totalPrice = calculateTotalPrice(cartList)
//        // Panggil fungsi updateTotalPrice di listener
//        listener.updateTotalPrice(totalPrice)
//    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_cart_food, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val cart = cartList[position]

        holder.salmon4.text = cart.menu_name
        holder.price4.text = cart.menu_price.toString()
//        holder.desc5.text = cart.menu_description
        Glide.with(holder.itemView.context)
            .load(cart.menu_image)
            .into(holder.restaurant4)

        holder.elegantNumberButton.number = cart.quantity.toString()

        holder.elegantNumberButton.setOnValueChangeListener { _, _, newValue ->
            cart.quantity = newValue.toInt()

//            updateTotalPrice()
        }
//        holder.drawablebed.setOnClickListener {
//            val itemId = cart.item_id
//            listener.deleteRequestByItemId(itemId)
//        }
//
//        holder.itemView.setOnClickListener{
//         listener.onClick(cart)
//        }

    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val salmon4 : TextView = itemView.findViewById(R.id.salmon4)
//        val desc5 : TextView = itemView.findViewById(R.id.desc5)
        val price4 : TextView = itemView.findViewById(R.id.price4)
        val restaurant4 : ImageView = itemView.findViewById(R.id.restaurant4)
        val drawablebed : View = itemView.findViewById(R.id.drawablebed)
        val elegantNumberButton: ElegantNumberButton = itemView.findViewById(R.id.elegantNumberButton) // Tambahkan deklarasi ElegantNumberButton di sini

    }

    // Define the updateTotalPrice() method
//    fun updateTotalPrice() {
//        totalPrice = calculateTotalPrice(cartList)
//        listener.updateTotalPrice(totalPrice)
//    }

    // Define the calculateTotalPrice() method
    private fun calculateTotalPrice(orderList: List<OrderX>): Int {
        var totalPrice = 0
        for (order in orderList) {
            totalPrice += order.menu_price * order.quantity
        }
        return totalPrice
    }
    fun setData(data: List<OrderX>) {
        cartList.clear()
        cartList.addAll(data)
        notifyDataSetChanged()
    }

    interface OnAdapterListener{
        fun onClick(cart : OrderX)
        fun deleteRequestByItemId(itemId: Int)
        fun updateTotalPrice(totalPrice: Int)
    }

}