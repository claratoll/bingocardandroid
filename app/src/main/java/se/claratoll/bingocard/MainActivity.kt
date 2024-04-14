package se.claratoll.bingocard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var mySharedPreferences: MySharedPreferences
    private lateinit var mainSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mySharedPreferences = MySharedPreferences(this)

        val toBingoButton = findViewById<Button>(R.id.toBingoButton)
        mainSpinner = findViewById(R.id.mainSpinner)

        val savedYears = mySharedPreferences.getAllYears().toList().toMutableList()
        savedYears.add("Starta nytt år")

        if (savedYears.isNotEmpty()) {
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, savedYears)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mainSpinner.adapter = adapter
        } else {
            val defaultOption = listOf("Starta nytt år")
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, defaultOption)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mainSpinner.adapter = adapter
        }

        var selectedYear = "Starta nytt år"

        mainSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedYear = parent?.getItemAtPosition(position).toString()
                toBingoButton.text = selectedYear
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Ingen åtgärd behövs när inget är valt
            }
        }

        mainSpinner.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            if (selectedItem != "Starta nytt år") {
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("Ta bort år")
                alertDialogBuilder.setMessage("Vill du ta bort $selectedItem?")
                alertDialogBuilder.setPositiveButton("Ja") { _, _ ->
                    // Ta bort år från listan
                    savedYears.remove(selectedItem)
                    val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, savedYears)
                    adapter.notifyDataSetChanged()
                }
                alertDialogBuilder.setNegativeButton("Avbryt") { dialog, _ ->
                    dialog.dismiss()
                }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }
            true
        }


        toBingoButton.setOnClickListener {
            if (selectedYear == "Starta nytt år") {
                changeValue(toBingoButton)
            } else {
                val intent = Intent(this@MainActivity, BingoActivity::class.java)
                intent.putExtra("selectedYear", selectedYear)
                startActivity(intent)
            }
        }
    }

    private fun changeValue(textViewToUpdate: TextView) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.inputfield, null)
        val changeEditText = dialogView.findViewById<EditText>(R.id.changeEditText)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)

        val dialog = dialogBuilder.create()
        dialog.show()

        saveButton.setOnClickListener {
            val newText = changeEditText.text.toString()
            textViewToUpdate.text = newText
            dialog.dismiss()
            val intent = Intent(this@MainActivity, BingoActivity::class.java)
            intent.putExtra("selectedYear", newText)
            startActivity(intent)
        }
    }
}