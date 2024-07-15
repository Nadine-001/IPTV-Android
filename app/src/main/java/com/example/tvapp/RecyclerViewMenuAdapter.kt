package com.example.tvapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecyclerViewMenuAdapter(private val menuList: MutableList<MenuModelApiItem>):
    RecyclerView.Adapter<RecyclerViewMenuAdapter.MyViewHolder>(),
    RecyclerViewMenuListAdapter.OnItemClickListener{

    private var listener: RecyclerViewMenuListAdapter.OnItemClickListener? = null

    fun setOnItemClickListener(listener: RecyclerViewMenuListAdapter.OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_menu_category, parent, false)

        return MyViewHolder(view)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val menu = menuList[position]

        holder.menu_type.text = menu.menu_type

        holder.rv_food.adapter = RecyclerViewMenuListAdapter(menu.menu)

        holder.itemView.setOnClickListener {
            listener?.onItemClick(holder.adapterPosition)
            Log.d("RecyclerViewAdapter", "Clicked item at position: $position")
        }//ini ya?
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menu_type : TextView = itemView.findViewById(R.id.menu_type)
        val rv_food: RecyclerView = itemView.findViewById(R.id.rv_food_list)
    }

    fun setData(data: List<MenuModelApiItem>) {
        menuList.clear()
        menuList.addAll(data)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    override fun onItemClick(position: Int) {
        listener?.onItemClick(position)
    }



}