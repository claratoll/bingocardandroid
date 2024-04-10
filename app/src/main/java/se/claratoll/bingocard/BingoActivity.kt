package se.claratoll.bingocard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BingoActivity : AppCompatActivity() {

    private lateinit var lastClickedTextView: TextView
    private val doubleClickTimeout: Long = 300
    private lateinit var mySharedPreferences: MySharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bingo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mySharedPreferences = MySharedPreferences(this)

        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                showChangePopup(lastClickedTextView)
                return true
            }
        })

        val parentView = findViewById<View>(R.id.main)

        val allTextViewIds = arrayOf(
            R.id.a1, R.id.a2, R.id.a3, R.id.a4, R.id.a5,
            R.id.b1, R.id.b2, R.id.b3, R.id.b4, R.id.b5,
            R.id.c1, R.id.c2, R.id.c3, R.id.c4, R.id.c5,
            R.id.d1, R.id.d2, R.id.d3, R.id.d4, R.id.d5,
            R.id.e1, R.id.e2, R.id.e3, R.id.e4, R.id.e5
        )
        for (textViewId in allTextViewIds) {
            val textView = findViewById<TextView>(textViewId)
            textView.text = mySharedPreferences.getText(textViewId.toString(), "2024")
            textView.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    lastClickedTextView = textView
                }
                gestureDetector.onTouchEvent(event)
                true
            }
        }
    }

    private fun showChangePopup(textViewToUpdate: TextView) {
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
            mySharedPreferences.saveText(textViewToUpdate.id.toString(), newText)
            dialog.dismiss()
        }
    }
}

class MySharedPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    // Metod för att spara texten från en TextView
    fun saveText(key: String, text: String) {
        sharedPreferences.edit().putString(key, text).apply()
    }

    // Metod för att hämta texten från en TextView
    fun getText(key: String, default: String): String {
        return sharedPreferences.getString(key, default) ?: default
    }
}