package com.example.tvapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tvapp.R

class CardAdapter : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    private val images = intArrayOf(
        R.drawable.restaurant,
        R.drawable.bar,
        R.drawable.gym,
        R.drawable.rooftop,
        R.drawable.working
    )

    private val text = arrayOf(
        "Restaurant",
        "Bar",
        "Gym",
        "Rooftop",
        "Working Space"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.facility, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemImage.setImageResource(images[position])
        holder.itemText.text = text[position]
    }

    override fun getItemCount(): Int {
        return images.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemImage: ImageView = itemView.findViewById(R.id.cardImage)
        var itemText: TextView = itemView.findViewById(R.id.cardText)
    }
}
