package com.thew00xer.temigorestaurantdemoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private var orderList: ArrayList<String>, private var orderListT: ArrayList<String>) : RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var orderNumber: TextView = view.findViewById(R.id.orderNumber)
        var tableNumber: TextView = view.findViewById(R.id.tableNumber)
        var timeStamp: TextView = view.findViewById(R.id.timeStamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = orderList[position]
        holder.orderNumber.text = ("Order " + (position + 1) + ":")
        holder.tableNumber.text = item
        holder.timeStamp.text = (orderListT[position])
    }
    override fun getItemCount(): Int {
        return orderList.size
    }
}