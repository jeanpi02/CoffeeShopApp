package com.jeanpi.coffeshopapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PaymentAdapter(private val paymentList: List<Payment>) :
    RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_payment, parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val payment = paymentList[position]
        holder.tvFecha.text = payment.fecha
        holder.tvMontoPagado.text = payment.montoPagado
    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvMontoPagado: TextView = itemView.findViewById(R.id.tvMontoPagado)
    }
}
