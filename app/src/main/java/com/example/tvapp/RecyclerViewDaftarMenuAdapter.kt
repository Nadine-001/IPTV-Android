package com.example.tvapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecyclerViewDaftarMenuAdapter(private val menuList: MutableList<DaftarMenuPerCategoryModelItem>, val listener : OnAdapterListener):
    RecyclerView.Adapter<RecyclerViewDaftarMenuAdapter.MyViewHolder>(){

    fun getMenuPosition(position: Int): DaftarMenuPerCategoryModelItem? {
        return if (position in 0 until menuList.size) {
            menuList[position]
        } else {
            null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_daftar_menu, parent, false)
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

        holder.itemView.setOnClickListener{
            listener.onClick(menu)
        }
        holder.cart.setOnClickListener{
            listener.addToCartClicked(it)
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
        val cart: TextView = itemView.findViewById(R.id.linkReferenceTextView2)
    }
    fun setData(data: List<DaftarMenuPerCategoryModelItem>) {
        menuList.clear() // Membersihkan data sebelum menambahkan yang baru
        menuList.addAll(data)
        notifyDataSetChanged()
//        notifyItemRangeInserted(startPosition, data.size) // Beri tahu adapter tentang data baru yang ditambahkan
    }

//    fun setData(data: List<DaftarMenuModelItem>) {
//        menuList.clear()
//        menuList.addAll(data)
//        notifyDataSetChanged()
//    }

    interface OnAdapterListener{
        fun onClick(service : DaftarMenuPerCategoryModelItem)
        fun addToCartClicked(view: View)
    }
}