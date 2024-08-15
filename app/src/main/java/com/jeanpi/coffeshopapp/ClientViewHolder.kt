package com.jeanpi.coffeshopapp

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView = itemView.findViewById(R.id.imageView)
    val idView: TextView = itemView.findViewById(R.id.idView)
    val nameView: TextView = itemView.findViewById(R.id.nameView)
    val descView: TextView = itemView.findViewById(R.id.descView)
}