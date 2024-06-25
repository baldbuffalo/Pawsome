package com.example.pawsome

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var userNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)

        // Enable the back button in the ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize views from profile_activity.xml
        userNameTextView = findViewById(R.id.userNameTextView)

        // Retrieve user's name from intent
        val userName = intent.getStringExtra("userName") ?: ""

        // Apply bold style to the username text using string resource
        userNameTextView.text = getString(R.string.user_name, userName)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Go back when the back button is pressed
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
