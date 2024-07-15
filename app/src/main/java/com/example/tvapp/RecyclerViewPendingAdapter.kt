package com.example.tvapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton

class RecyclerViewPendingAdapter(private var pendingList : List<UnpaidOrder>) :
    RecyclerView.Adapter<RecyclerViewPendingAdapter.MyViewHolder>(){


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_pending, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pending = pendingList[position]

        holder.salmon4.text = pending.menu_name
        holder.price4.text = pending.menu_price.toString()
        holder.desc5.text = pending.menu_description
        Glide.with(holder.itemView.context)
            .load(pending.menu_image)
            .into(holder.restaurant4)
        holder.quantity.text = pending.quantity.toString()


    }

    override fun getItemCount(): Int {
        return pendingList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val salmon4 : TextView = itemView.findViewById(R.id.salmon4)
        val desc5 : TextView = itemView.findViewById(R.id.desc5)
        val price4 : TextView = itemView.findViewById(R.id.price4)
        val quantity : TextView = itemView.findViewById(R.id.quantity)
        val restaurant4 : ImageView = itemView.findViewById(R.id.restaurant4)


    }



    fun setData(data: List<UnpaidOrder>) {
        pendingList = data
        notifyDataSetChanged()
    }

    interface OnAdapterListener{
        fun onClick(cart : UnpaidOrder)
    }

}