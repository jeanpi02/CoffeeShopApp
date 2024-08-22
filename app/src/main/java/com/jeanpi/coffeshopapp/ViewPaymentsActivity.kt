package com.jeanpi.coffeshopapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException

class ViewPaymentsActivity : AppCompatActivity() {

    private val paymentList = mutableListOf<Payment>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var paymentAdapter: PaymentAdapter
    private lateinit var tvClient: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_payments)

        tvClient = findViewById(R.id.tvCliente)

        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewBuys)
        recyclerView.layoutManager = LinearLayoutManager(this)
        paymentAdapter = PaymentAdapter(paymentList)
        recyclerView.adapter = paymentAdapter

        // Obtener el ID del cliente desde el Intent
        val clienteId = intent.getIntExtra("id", -1)
        val clientName = intent.getStringExtra("name").toString()
        tvClient.text = clientName

        if (clienteId != -1) {
            fetchPayments(clienteId)
        } else {
            Toast.makeText(this, "ID de cliente no válido", Toast.LENGTH_LONG).show()
        }
    }

    private fun fetchPayments(clienteId: Int) {
        val url = "https://api-flask-fqgf.onrender.com/getPayments/$clienteId"
        val volleySingleton = VolleySingleton.getInstance(this)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    paymentList.clear() // Limpiar la lista antes de agregar nuevos datos

                    // Verificar si hay pagos en la respuesta
                    val pagosArray = response.optJSONArray("pagos")
                    if (pagosArray != null && pagosArray.length() > 0) {
                        for (i in 0 until pagosArray.length()) {
                            val pago = pagosArray.getJSONObject(i)
                            val fecha = pago.getString("fecha")
                            val montoPagado = pago.getString("monto_pagado")

                            val payment = Payment(fecha, montoPagado)
                            paymentList.add(payment)
                        }
                    } else if (response.has("message")) {
                        Toast.makeText(this, response.getString("message"), Toast.LENGTH_LONG)
                            .show()
                    }

                    // Actualizar el RecyclerView con los datos recibidos
                    paymentAdapter.notifyDataSetChanged()

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al procesar los datos", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(
                    this,
                    "Error al obtener los pagos: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        volleySingleton.requestQueue.add(jsonObjectRequest)
    }
}
