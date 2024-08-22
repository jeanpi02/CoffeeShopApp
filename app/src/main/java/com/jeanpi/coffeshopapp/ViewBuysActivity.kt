package com.jeanpi.coffeshopapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONException
import org.json.JSONObject

class ViewBuysActivity : AppCompatActivity() {

    private val buyList = mutableListOf<Buy>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var buyAdapter: BuyAdapter
    private lateinit var tvClient: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_buys)

        // Configurar los márgenes de la vista principal
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        tvClient = findViewById(R.id.tvCliente)

        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewBuys)
        recyclerView.layoutManager = LinearLayoutManager(this)
        buyAdapter = BuyAdapter(buyList)
        recyclerView.adapter = buyAdapter

        // Llamada para obtener las compras
        val clienteId = intent.getIntExtra("id", -1) // Obtener el ID del cliente del intent
        val clientName = intent.getStringExtra("name").toString()
        tvClient.text = clientName

        if (clienteId != -1) {
            fetchBuys(clienteId)
        } else {
            Toast.makeText(this, "ID de cliente no válido", Toast.LENGTH_LONG).show()
        }
    }

    private fun fetchBuys(clienteId: Int) {
        val url = "https://api-flask-fqgf.onrender.com/getBuys/$clienteId"
        val volleySingleton = VolleySingleton.getInstance(this)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Limpiar la lista antes de agregar nuevos datos
                    buyList.clear()

                    if (response.length() == 0) {
                        Toast.makeText(
                            this,
                            "No se encontraron compras registradas",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        for (i in 0 until response.length()) {
                            val jsonObject = response.getJSONObject(i)
                            val tipoPago = jsonObject.getString("tipo_pago")
                            val fecha = jsonObject.getString("fecha")
                            val producto = jsonObject.getString("producto")
                            val precio = jsonObject.getString("costo")

                            val buy = Buy(tipoPago, fecha, producto, precio)
                            buyList.add(buy)
                        }

                        // Actualizar el RecyclerView con los datos recibidos
                        buyAdapter.notifyDataSetChanged()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al procesar los datos", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                // Manejar el caso donde la respuesta no es un arreglo JSON
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    val responseData = String(error.networkResponse.data)
                    try {
                        val jsonObject = JSONObject(responseData)
                        val errorMessage = jsonObject.optString("message", "Error desconocido")
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this,
                            "Error al procesar la respuesta del servidor",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Error al obtener las compras: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )

        volleySingleton.requestQueue.add(jsonArrayRequest)
    }


}
