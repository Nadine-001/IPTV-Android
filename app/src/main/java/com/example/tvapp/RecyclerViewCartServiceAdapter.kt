package com.example.tvapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton

class RecyclerViewCartServiceAdapter(
    private val cartList: MutableList<CartServiceModelItem>,
//    private val listener: OnAdapterListener
) : RecyclerView.Adapter<RecyclerViewCartServiceAdapter.MyViewHolder>() {

    private var selectedItemId: Int? = null
    fun getServiceDataAtPosition(position: Int): CartServiceModelItem? {
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

//    private var selectedPosition = RecyclerView.NO_POSITION

//    fun updateSelectedPosition(newPosition: Int) {
//        val previousPosition = selectedPosition
//        selectedPosition = newPosition
//        notifyItemChanged(previousPosition)
//        notifyItemChanged(newPosition)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_cart, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val cart = cartList[position]

        holder.salmon4.text = cart.room_service_name
        Glide.with(holder.itemView.context)
            .load(cart.room_service_image)
            .into(holder.restaurant4)

        // Set nilai awal untuk ElegantNumberButton
        holder.elegantNumberButton.number = cart.quantity.toString()

        // Tambahkan event listener untuk mengubah nilai quantity saat ElegantNumberButton diubah
        holder.elegantNumberButton.setOnValueChangeListener { _, _, newValue ->
            cart.quantity = newValue.toInt()
        }

        // Set skala drawablebed berdasarkan selectedItemId
//        if (cart.item_id == selectedItemId) {
//            holder.drawablebed.scaleX = 1.5f
//            holder.drawablebed.scaleY = 1.5f
//        } else {
//            holder.drawablebed.scaleX = 1f
//            holder.drawablebed.scaleY = 1f
//        }

//        holder.drawablebed.setOnClickListener {
//            val itemId = cart.item_id
//            listener.deleteRequestByItemId(itemId)
//        }
//
//        holder.itemView.setOnClickListener {
//            listener.onClick(cart)
//            updateSelectedPosition(holder.adapterPosition)
//        }
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val salmon4: TextView = itemView.findViewById(R.id.salmon4)
        val restaurant4: ImageView = itemView.findViewById(R.id.restaurant4)
        val drawablebed: View = itemView.findViewById(R.id.drawablebed)
        val elegantNumberButton: ElegantNumberButton = itemView.findViewById(R.id.elegantNumberButton)
    }

    fun setData(data: List<CartServiceModelItem>) {
        cartList.clear()
        cartList.addAll(data)
        notifyDataSetChanged()
    }

    interface OnAdapterListener {
        fun onClick(cart: CartServiceModelItem)
        fun deleteRequestByItemId(itemId: Int)
    }
}
