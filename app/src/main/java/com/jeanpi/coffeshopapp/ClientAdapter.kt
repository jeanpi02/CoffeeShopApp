package com.jeanpi.coffeshopapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ClientAdapter(private val clients: List<Client>) : RecyclerView.Adapter<ClientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.client_item, parent, false)
        return ClientViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val cliente = clients[position]
        holder.imageView.setImageResource(R.drawable.user_svgrepo_com)
        holder.nameView.text = cliente.name
        holder.descView.text = cliente.desc
        holder.idView.text = "id : ${cliente.id.toString()}"

        // Set onClickListener
        holder.itemView.setOnClickListener {
            // Crear un Intent para iniciar la nueva actividad
            val context = holder.itemView.context
            val intent = Intent(context, ClientOptionsActivity::class.java)

            // Pasar los datos del cliente a la nueva actividad
            intent.putExtra("id", cliente.id)
            intent.putExtra("name", cliente.name)

            // Iniciar la actividad
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return clients.size
    }
}
