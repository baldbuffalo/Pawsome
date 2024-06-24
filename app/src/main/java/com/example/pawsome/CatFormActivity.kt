package com.example.pawsome

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class CatFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cat_form_activity)

        val catImageView: ImageView = findViewById(R.id.catImageView)

        val imageUri = intent.getParcelableExtra<Uri>("imageUri")
        imageUri?.let {
            catImageView.setImageURI(it)
        }
    }
}
