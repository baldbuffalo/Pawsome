package com.example.pawsome

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var userNameTextView: TextView
    private lateinit var backArrowImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)

        // Initialize views from profile_activity.xml
        userNameTextView = findViewById(R.id.userNameTextView)
        backArrowImageView = findViewById(R.id.backArrowImageView)

        // Retrieve user's name from intent
        val userName = intent.getStringExtra("userName") ?: ""

        // Apply bold style to the username text using string resource
        userNameTextView.text = getString(R.string.user_name, userName)

        // Set click listener for back arrow ImageView
        backArrowImageView.setOnClickListener {
            onBackPressed() // Navigate back to previous activity
        }
    }
}
