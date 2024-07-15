package com.example.tvapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecyclerViewMenuListAdapter(private val menuList: List<Menu>):
    RecyclerView.Adapter<RecyclerViewMenuListAdapter.MyViewHolder>(){

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    private var focusedPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_menu_list, parent, false)
        return MyViewHolder(view)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val menu = menuList[position]

        holder.menu_name.text = menu.menu_name
        holder.menu_description.text = menu.menu_description
        holder.menu_price.text = menu.menu_price.toString()
        Glide.with(holder.itemView.context)
            .load(menu.menu_image)
            .into(holder.menu_image)

        holder.itemView.setOnClickListener {
            listener?.onItemClick(holder.adapterPosition)
            Log.d("RecyclerViewMenuListAdapter", "Clicked item at position: $position")
        }
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menu_name: TextView = itemView.findViewById(R.id.menu_name)
        val menu_description: TextView = itemView.findViewById(R.id.menu_description)
        val menu_price: TextView = itemView.findViewById(R.id.menu_price)
        val menu_image: ImageView = itemView.findViewById(R.id.menu_image)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }


}