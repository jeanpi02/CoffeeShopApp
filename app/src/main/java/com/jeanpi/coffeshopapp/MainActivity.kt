package com.jeanpi.coffeshopapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnView1 = findViewById<Button>(R.id.btnViewClients)
        val btnAddClientView = findViewById<Button>(R.id.btnAddClient)

        btnView1.setOnClickListener {
            navigateToView1()
        }
        btnAddClientView.setOnClickListener {
            navigateToAddClientView()
        }

    }

    private fun navigateToAddClientView() {
        val intent = Intent(this, AddClientActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToView1() {
        val intent = Intent(this, ClientActivity::class.java)
        startActivity(intent)
    }


}