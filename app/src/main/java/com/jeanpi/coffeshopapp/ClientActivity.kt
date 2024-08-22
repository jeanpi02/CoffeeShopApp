package com.jeanpi.coffeshopapp

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray
import org.json.JSONException


class ClientActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var clients: MutableList<Client>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cliente)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recyclerViewMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initRecyclerView()
        fetchClients()
    }

    // Inicializa el RecyclerView y la lista de clientes
    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        clients = mutableListOf()
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    // Realiza la solicitud para obtener los clientes
    private fun fetchClients() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_LONG).show()
            return
        }

        val url = "https://api-flask-fqgf.onrender.com/getClients"
        val volleySingleton = VolleySingleton.getInstance(this)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                parseClientResponse(response)
                setupRecyclerViewAdapter()
            },
            { error ->
                Log.e("VolleyError", "Request error: ${error.message}")
            }
        )

        volleySingleton.requestQueue.add(jsonArrayRequest)
    }


    // Parsea la respuesta JSON y llena la lista de clientes
    private fun parseClientResponse(response: JSONArray) {
        try {
            for (i in 0 until response.length()) {
                val jsonObject = response.getJSONObject(i)
                val name = jsonObject.getString("nombre")
                val desc = jsonObject.getString("saldo")
                val id = jsonObject.getInt("id")
                val client = Client(name = name, desc = desc, id = id)
                clients.add(client)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e("VolleyError", "JSON Parsing error: ${e.message}")
        }
    }

    // Configura el adaptador del RecyclerView después de llenar la lista
    private fun setupRecyclerViewAdapter() {
        recyclerView.adapter = ClientAdapter(clients)
    }

    @Suppress("DEPRECATION")
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
