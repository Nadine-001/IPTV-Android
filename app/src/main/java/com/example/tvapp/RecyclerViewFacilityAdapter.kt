package com.example.tvapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecyclerViewFacilityAdapter(private val facilityList: MutableList<FacilityModelItem>) :
    RecyclerView.Adapter<RecyclerViewFacilityAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_facility, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return facilityList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val facility = facilityList[position]

        // Set image using Glide or another image loading library

        Glide.with(holder.itemView.context)
            .load(facility.facility_image)
            .into(holder.rvfacilityimg)


        // Set facility name
        holder.rvfacilitytitle.text = facility.facility_name
        holder.rvfacilitydesc.text = facility.facility_description
//        holder.rvfacilityimg.setImageResource = facility.facility_image
//        holder.rvfacilitytitle.text = facilityList[position].facility_name
//        holder.rvfacilityimg.setImageResource(facilityList[position].facility_image)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvfacilitytitle : TextView = itemView.findViewById(R.id.rvfacilitytitle)
        val rvfacilitydesc : TextView = itemView.findViewById(R.id.rvfacilitydesc)
        val rvfacilityimg : ImageView = itemView.findViewById(R.id.rvfacilityimg)
        val cardView : CardView = itemView.findViewById(R.id.cardView)

    }

    fun setData(data: List<FacilityModelItem>) {
        facilityList.clear()
        facilityList.addAll(data)
        notifyDataSetChanged()
    }

//    fun setData(data: List<FacilityModelItem>) {
//        facilityList.addAll(data)
//        notifyDataSetChanged()
//    }



}

//<html>Unresolved reference. None of the following candidates is applicable because of receiver type mismatch:<br/>public fun &lt;T&gt; MutableCollection&lt;in TypeVariable(T)&gt;.addAll(elements: Array&lt;out TypeVariable(T)&gt;): Boolean defined in kotlin.collections<br/>public fun &lt;T&gt; MutableCollection&lt;in TypeVariable(T)&gt;.addAll(elements: Iterable&lt;TypeVariable(T)&gt;): Boolean defined in kotlin.collections<br/>public fun &lt;T&gt; MutableCollection&lt;in TypeVariable(T)&gt;.addAll(elements: Sequence&lt;TypeVariable(T)&gt;): Boolean defined in kotlin.collections
//<html>Unresolved reference. None of the following candidates is applicable because of receiver type mismatch:<br/>public fun kotlin.text.StringBuilder /* = java.lang.StringBuilder */.clear(): kotlin.text.StringBuilder /* = java.lang.StringBuilder */ defined in kotlin.text