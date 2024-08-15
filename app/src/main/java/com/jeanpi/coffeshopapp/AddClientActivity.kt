package com.jeanpi.coffeshopapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.jeanpi.coffeshopapp.R.id.edtName
import org.json.JSONException
import org.json.JSONObject

class AddClientActivity : AppCompatActivity() {
    private lateinit var edtName: EditText
    private lateinit var edtBalance: EditText
    private lateinit var btnAddClient:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_client)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addClientLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnAddClient = findViewById(R.id.btnSaveUser)

        // Inicializar EditTexts
        edtName = findViewById(R.id.edtName)
        edtBalance = findViewById(R.id.edtBalance)

        // Configurar el listener del botón
        btnAddClient.setOnClickListener {
            val name = edtName.text.toString()
            val saldo = edtBalance.text.toString()

            val requestBody = JSONObject().apply {
                put("nombre", name)
                put("saldo", saldo)
            }

            SendPostNewCliente(requestBody)
        }
    }

    private fun SendPostNewCliente(requestBody: JSONObject) {
        val url = "https://smoothly-welcomed-hen.ngrok-free.app/saveClient"

        val volleySingleton = VolleySingleton.getInstance(this)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            { response ->
                val message = response.getString("message".toString())
                Toast.makeText(this, "Client agregado: $message", Toast.LENGTH_LONG).show()
            },
            { error ->
                error.printStackTrace()
                val errorMsg = error.message ?: "Unknown error"
                Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
            }
        )

        volleySingleton.requestQueue.add(jsonObjectRequest)
    }

}