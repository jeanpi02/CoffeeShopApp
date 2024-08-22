package com.jeanpi.coffeshopapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BuyAdapter(private val buyList: List<Buy>) :
    RecyclerView.Adapter<BuyAdapter.BuyViewHolder>() {

    class BuyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tipoPagoTextView: TextView = itemView.findViewById(R.id.tvTipoPago)
        val fechaTextView: TextView = itemView.findViewById(R.id.tvFecha)
        val productoTextView: TextView = itemView.findViewById(R.id.tvProducto)
        val precioTextView: TextView = itemView.findViewById(R.id.tvPrecio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_buy, parent, false)
        return BuyViewHolder(view)
    }

    override fun onBindViewHolder(holder: BuyViewHolder, position: Int) {
        val buy = buyList[position]
        holder.tipoPagoTextView.text = buy.tipoPago
        holder.fechaTextView.text = buy.fecha
        holder.productoTextView.text = buy.producto
        holder.precioTextView.text = buy.precio
    }

    override fun getItemCount(): Int {
        return buyList.size
    }
}
