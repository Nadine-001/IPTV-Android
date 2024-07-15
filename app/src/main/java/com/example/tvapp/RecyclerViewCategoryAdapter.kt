package com.example.tvapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecyclerViewCategoryAdapter(private val menuList: MutableList<MenuType>):
    RecyclerView.Adapter<RecyclerViewCategoryAdapter.MyViewHolder>(){

    fun getCategoryPosition(position: Int): MenuType? {
        return if (position in 0 until menuList.size) {
            menuList[position]
        } else {
            null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_category_list
            , parent, false)
        return MyViewHolder(view)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val menu = menuList[position]
//        holder.bind(menu)
        holder.menu_name.text = menu.menu_type
//        holder.itemView.setOnClickListener{
//            listener?.onClick(menu)
//        }
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menu_name: TextView = itemView.findViewById(R.id.menu_name)

        fun bind(menu: MenuType) {
            menu_name.text = menu.menu_type
            // You can bind other views here if needed
        }
    }

    fun setData(data: List<MenuType>) {
        menuList.clear()
        menuList.addAll(data)
        notifyDataSetChanged()
    }

    interface OnAdapterListener{
        fun onClick(category : MenuType)
    }

    // Method untuk menetapkan listener
//    fun setOnAdapterListener(listener: OnAdapterListener) {
//        this.listener = listener
//    }
}