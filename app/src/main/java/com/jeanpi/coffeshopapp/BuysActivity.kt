package com.jeanpi.coffeshopapp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class BuysActivity : AppCompatActivity() {
    var id = 0
    private lateinit var btnAddBuy: Button
    private lateinit var btnShowDetail: ImageButton
    private lateinit var txtFechaSistema: TextView
    private lateinit var etName: TextView
    private lateinit var tvTotal:TextView
    private lateinit var productoEditText: EditText
    private lateinit var precioEditText: EditText
    private lateinit var agregarButton: Button
    private lateinit var tableLayout: TableLayout
    private lateinit var currentDate: String
    private val buyList = mutableListOf<Buy>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_buys)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.viewCompras)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponents()
        setupListeners()
        displayCurrentDate()
        retrieveIntentData()
    }

    private fun initComponents() {
        txtFechaSistema = findViewById(R.id.etFecha)
        etName = findViewById(R.id.tvCliente)
        productoEditText = findViewById(R.id.etProducto)
        precioEditText = findViewById(R.id.etPrecio)
        agregarButton = findViewById(R.id.btnAdd)
        tableLayout = findViewById(R.id.tableProducts)
        btnAddBuy = findViewById(R.id.btnSaveBuy)
        btnShowDetail = findViewById(R.id.btnDetail)
        currentDate = getCurrentDate()
        tvTotal = findViewById(R.id.totalC)
    }

    private fun setupListeners() {
        agregarButton.setOnClickListener {
            if (validateInputs()) {
                addRowToTable()
                clearInputs()
            }
        }

        btnAddBuy.setOnClickListener {
            sendBuys()
        }

        btnShowDetail.setOnClickListener {
            navigateToViewBuys()
        }
    }

    private fun navigateToViewBuys() {
        val intent = Intent(this, ViewBuysActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }

    private fun validateInputs(): Boolean {
        val producto = productoEditText.text.toString()
        val precio = precioEditText.text.toString()

        if (producto.isEmpty()) {
            productoEditText.error = "El producto no puede estar vacío"
            return false
        }

        if (precio.isEmpty()) {
            precioEditText.error = "El precio no puede estar vacío"
            return false
        }

        val precioNumero = precio.toDoubleOrNull()
        if (precioNumero == null || precioNumero <= 0) {
            precioEditText.error = "Ingrese un precio válido"
            return false
        }

        if (!findViewById<RadioButton>(R.id.rbPagado).isChecked && !findViewById<RadioButton>(R.id.rbFiado).isChecked) {
            Toast.makeText(this, "Seleccione un tipo de pago", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun addRowToTable() {
        val producto = productoEditText.text.toString()
        val precio = precioEditText.text.toString()
        val fecha = txtFechaSistema.text.toString()
        val tipoPago = if (findViewById<RadioButton>(R.id.rbPagado).isChecked) "Pagado" else "Fiado"

        // Crear un objeto Buy y agregarlo a la lista
        val buy = Buy(tipoPago, fecha, producto, precio)
        buyList.add(buy)

        // Repintar la tabla
        repaintTable()
    }

    private fun removeRowFromTable(buy: Buy) {
        buyList.remove(buy)
        repaintTable()  // Repintar la tabla después de eliminar el elemento
    }

    private fun repaintTable() {
        // Guardar el encabezado
        val header = tableLayout.getChildAt(0)

        // Eliminar todas las filas excepto el encabezado
        tableLayout.removeAllViews()
        tableLayout.addView(header)

        // Volver a agregar todas las filas desde buyList
        for (buy in buyList) {
            val row = TableRow(this).apply {
                addView(createTextView(buy.tipoPago))
                addView(createTextView(buy.fecha))
                addView(createTextView(buy.producto))
                addView(createTextView(buy.precio))


                val deleteButton = ImageButton(this@BuysActivity).apply {
                    setImageResource(R.drawable.remove_bin_delete_trash_svgrepo_com)

                    // Opcional: Establecer un fondo transparente si solo quieres mostrar el ícono
                    background = null

                    setOnClickListener {
                        AlertDialog.Builder(this@BuysActivity)
                            .setTitle("Eliminar fila")
                            .setMessage("¿Estás seguro de que deseas eliminar esta fila?")
                            .setPositiveButton("Sí") { _, _ ->
                                removeRowFromTable(buy)
                            }
                            .setNegativeButton("No", null)
                            .show()
                    }
                }


                addView(deleteButton)
            }

            // Añadir la fila a la tabla
            tableLayout.addView(row)
        }
        // Actualizar el total después de repintar la tabla
        updateTotal()
    }

    private fun updateTotal() {
        var total = 0.0
        for (buy in buyList) {
            total += buy.precio.toDoubleOrNull() ?: 0.0
        }
        tvTotal.text = "$ $total"
    }


    private fun clearInputs() {
        productoEditText.text.clear()
        precioEditText.text.clear()
        findViewById<RadioGroup>(R.id.rbtn).clearCheck()
    }

    private fun sendBuys() {
        if (buyList.isEmpty()) {
            Toast.makeText(this, "No hay compras para guardar", Toast.LENGTH_SHORT).show()
            return
        }

        var allRequestsSucceeded = true

        for (i in 1 until tableLayout.childCount) {
            val row = tableLayout.getChildAt(i) as TableRow
            val tipoPago = (row.getChildAt(0) as TextView).text.toString()
            val fecha = (row.getChildAt(1) as TextView).text.toString()
            val producto = (row.getChildAt(2) as TextView).text.toString()
            val precio = (row.getChildAt(3) as TextView).text.toString().toDoubleOrNull()

            if (precio == null) {
                Toast.makeText(this, "El precio de un producto es inválido", Toast.LENGTH_LONG).show()
                allRequestsSucceeded = false
                break
            }

            val requestBody = JSONObject().apply {
                put("cliente_id", id)
                put("producto", producto)
                put("costo", precio)
                put("fecha", fecha)
                put("tipo_pago", tipoPago)
            }

            val volleySingleton = VolleySingleton.getInstance(this)
            val request = JsonObjectRequest(
                Request.Method.POST, "https://smoothly-welcomed-hen.ngrok-free.app/saveBuy", requestBody,
                { response ->
                    val successMessage = response.optString("message", "Compra guardada correctamente")
                    Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
                    buyList.clear()
                    repaintTable()
                },
                { error ->
                    allRequestsSucceeded = false
                    Toast.makeText(this, "Error al guardar la compra: ${error.message}", Toast.LENGTH_LONG).show()
                }
            )
            volleySingleton.requestQueue.add(request)
        }

        if (allRequestsSucceeded) {
            Toast.makeText(this, "Todas las compras se guardaron correctamente", Toast.LENGTH_LONG).show()
        }
    }


    private fun displayCurrentDate() {
        txtFechaSistema.text =currentDate
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun retrieveIntentData() {
        id = intent.getIntExtra("id", -1)
        val clienteName = intent.getStringExtra("name").toString()
        etName.text = clienteName
    }



    private fun createTextView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            setPadding(8, 8, 8, 8)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
    }

    private fun SendPostNewBuy(jsonBody: JSONObject) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_LONG).show()
            return
        }

        val url = "https://smoothly-welcomed-hen.ngrok-free.app/saveBuy"

        val volleySingleton = VolleySingleton.getInstance(this)

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                Toast.makeText(this, response.getString("message"), Toast.LENGTH_LONG).show()
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        volleySingleton.requestQueue.add(request)
    }
    @Suppress("DEPRECATION")
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}


