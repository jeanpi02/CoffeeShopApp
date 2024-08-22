package com.jeanpi.coffeshopapp

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ClientAdapter(private val clients: List<Client>) : RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val idView: TextView = itemView.findViewById(R.id.idView)
        val nameView: TextView = itemView.findViewById(R.id.nameView)
        val descView: TextView = itemView.findViewById(R.id.descView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.client_item, parent, false)
        return ClientViewHolder(itemView)
    }


    @SuppressLint("ObjectAnimatorBinding")
    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val cliente = clients[position]
        holder.imageView.setImageResource(R.drawable.user_svgrepo_com)
        holder.nameView.text = cliente.name
        holder.descView.text = "$" + cliente.desc
        holder.idView.text = "id : ${cliente.id}"

        // Configurar el listener de toque con animación
        holder.itemView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Crear un ColorDrawable para animar el color de fondo
                    val background = ColorDrawable(ContextCompat.getColor(v.context, R.color.selected_item_color))
                    v.background = background

                    // Crear la animación de alpha (de transparente a opaco)
                    val animator = ObjectAnimator.ofFloat(background, "alpha", 0f, 1f)
                    animator.duration = 300 // Duración de la animación en milisegundos
                    animator.start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Crear la animación de alpha (de opaco a transparente) al soltar
                    val background = v.background as ColorDrawable
                    val animator = ObjectAnimator.ofFloat(background, "alpha", 1f, 0f)
                    animator.duration = 300 // Duración de la animación en milisegundos
                    animator.start()

                    // Restaurar el color de fondo original después de la animación
                    animator.addListener(object : android.animation.AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            v.setBackgroundColor(ContextCompat.getColor(v.context, R.color.white))
                        }
                    })

                    // Si fue un ACTION_UP (es decir, un click), iniciar la nueva actividad
                    if (event.action == MotionEvent.ACTION_UP) {
                        val context = v.context
                        val intent = Intent(context, ClientOptionsActivity::class.java)
                        intent.putExtra("id", cliente.id)
                        intent.putExtra("name", cliente.name)
                        context.startActivity(intent)
                    }
                }
            }
            true // Indicar que el evento fue manejado
        }
    }



    override fun getItemCount(): Int {
        return clients.size
    }
}
