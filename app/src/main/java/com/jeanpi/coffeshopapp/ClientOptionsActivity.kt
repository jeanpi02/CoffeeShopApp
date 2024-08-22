package com.jeanpi.coffeshopapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ClientOptionsActivity : AppCompatActivity() {
    private lateinit var btnSales: Button
    private lateinit var btnShowBuys: Button
    private lateinit var btnPayments: Button
    private lateinit var tvClient: TextView
    private lateinit var btnShowPayR: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_options)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btnSales = findViewById(R.id.btnSales)
        btnShowBuys = findViewById(R.id.btnShowBuys)
        btnPayments = findViewById(R.id.btnPayments)
        btnShowPayR = findViewById(R.id.btnShowPayR)
        tvClient = findViewById(R.id.tvClient)

        val clientId = intent.getIntExtra("id", -1) // Obtener el ID del cliente del intent
        val clientName = intent.getStringExtra("name").toString()

        tvClient.text = clientName

        val intentSales = Intent(this, BuysActivity::class.java)
        btnSales.setOnClickListener {
            intentSales.putExtra("id", clientId)
            intentSales.putExtra("name", clientName)
            startActivity(intentSales)
        }

        val intentShowBuys = Intent(this, ViewBuysActivity::class.java)
        btnShowBuys.setOnClickListener {
            intentShowBuys.putExtra("id", clientId)
            intentShowBuys.putExtra("name", clientName)
            startActivity(intentShowBuys)
        }

        val intentPayments = Intent(this, RegisterPaymentActivity::class.java)
        btnPayments.setOnClickListener {
            intentPayments.putExtra("id", clientId)
            intentPayments.putExtra("name", clientName)
            startActivity(intentPayments)
        }

        val intentShowPayR = Intent(this, ViewPaymentsActivity::class.java)
        btnShowPayR.setOnClickListener {
            intentShowPayR.putExtra("id", clientId)
            intentShowPayR.putExtra("name", clientName)
            startActivity(intentShowPayR)
        }
    }
}