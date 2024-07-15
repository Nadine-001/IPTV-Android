package com.example.tvapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecyclerViewChannelAdapter(private val channelList: MutableList<ChannelModelItem>, val listener : OnAdapterListener):
    RecyclerView.Adapter<RecyclerViewChannelAdapter.MyViewHolder>(){
    fun getChannelDataAtPosition(position: Int): ChannelModelItem? {
        return if (position in 0 until channelList.size) {
            channelList[position]
        } else {
            null
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_channel
            , parent, false)
        return MyViewHolder(view)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val channel = channelList[position]

        holder.channel.text = channel.name
        holder.itemView.setOnClickListener{
            listener.onClick(channel)
        }
    }

    override fun getItemCount(): Int {
        return channelList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val channel: TextView = itemView.findViewById(R.id.rvname)
    }

    fun setData(data: List<ChannelModelItem>) {
        channelList.clear()
        channelList.addAll(data)
        notifyDataSetChanged()
    }

    interface OnAdapterListener{
        fun onClick(service : ChannelModelItem)
    }
}