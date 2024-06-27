package com.example.pawsome

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide


class CatFormActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var catImageView: ImageView
    private lateinit var textViewConfirmation: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cat_form_activity)

        editTextName = findViewById(R.id.editTextName)
        editTextEmail = findViewById(R.id.editTextEmail)
        catImageView = findViewById(R.id.catImageView)
        textViewConfirmation = findViewById(R.id.textViewConfirmation)

        // Load image if available
        val imageUri = intent.getParcelableExtra<Uri>("imageUri")
        imageUri?.let {
            Glide.with(this)
                .load(it)
                .into(catImageView)
        }

        val buttonSubmit = findViewById<View>(R.id.buttonSubmit)
        buttonSubmit?.setOnClickListener {
            submitForm()
        }
    }

    private fun submitForm() {
        val name = editTextName.text.toString().trim()
        val email = editTextEmail.text.toString().trim()

        if (name.isEmpty() || email.isEmpty()) {
            // Show a warning message if any field is empty
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Simulate post submission
        simulatePost()
    }

    private fun simulatePost() {
        // Show confirmation message
        textViewConfirmation.visibility = View.VISIBLE

        // You can optionally clear the form fields here
        editTextName.text.clear()
        editTextEmail.text.clear()

        // Hide the confirmation message after a delay
        textViewConfirmation.postDelayed({
            textViewConfirmation.visibility = View.GONE
        }, 3000) // 3 seconds delay to hide the confirmation message
    }
}
