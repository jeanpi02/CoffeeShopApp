package com.jeanpi.coffeshopapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class RegisterPaymentActivity : AppCompatActivity() {
    private lateinit var etMontoPagado: EditText
    private lateinit var btnRegistrarPago: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_payment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        // Obtener el ID del cliente desde el intent
        val clienteId = intent.getIntExtra("id", -1)
        if (clienteId == -1) {
            Toast.makeText(this, "ID de cliente no válido", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        etMontoPagado = findViewById(R.id.etMontoPagado)
        btnRegistrarPago = findViewById(R.id.btnRegistrarPago)

        btnRegistrarPago.setOnClickListener {
            val montoPagado = etMontoPagado.text.toString().toDoubleOrNull()

            if (montoPagado != null && montoPagado > 0) {
                registerPayment(clienteId, montoPagado)
            } else {
                Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun registerPayment(clienteId: Int, montoPagado: Double) {
        val url = "https://api-flask-fqgf.onrender.com/registerPayment"
        val requestBody = JSONObject().apply {
            put("cliente_id", clienteId)
            put("monto_pagado", montoPagado)
        }

        val volleySingleton = VolleySingleton.getInstance(this)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            { response ->
                val successMessage = response.optString("message", "Pago registrado correctamente")
                Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show()
                finish() // Finalizar la actividad después de registrar el pago
            },
            { error ->
                error.printStackTrace()

                // Extraer el mensaje de error del servidor
                val errorMessage = error.networkResponse?.data?.let { data ->
                    try {
                        // Convertir los bytes a String y luego a JSONObject
                        val errorJson = JSONObject(String(data))
                        errorJson.optString("error", "Error al registrar el pago")
                    } catch (e: Exception) {
                        "Error al registrar el pago"
                    }
                } ?: "Error al registrar el pago"

                Toast.makeText(
                    this,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        volleySingleton.requestQueue.add(jsonObjectRequest)
    }

}