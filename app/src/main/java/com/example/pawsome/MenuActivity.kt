package com.example.pawsome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.pawsome.databinding.MenuActivityBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: MenuActivityBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data!!)
            handleSignInResult(task)
        } else {
            // Handle sign-in cancellation or error
            Log.e("Menu", "Sign-in cancelled or failed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MenuActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure Google Sign-In options with your server's client ID (replace with your actual ID)
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.your_server_client_id)) // Use empty string if null
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.signInButton.setOnClickListener {
            signIn()
        }

        // Check if the user is already signed in
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            // User is already signed in, go directly to MainActivity
            goToMainActivity(account)
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                // Sign-in successful
                goToMainActivity(account)
            } else {
                // Sign-in failed
                Log.e("Menu", "Sign-in failed: Account is null")
            }
        } catch (e: ApiException) {
            // Sign-in failed
            Log.e("Menu", "Sign-in failed:", e)
        }
    }

    private fun goToMainActivity(account: GoogleSignInAccount) {
        val idToken = account.idToken
        val displayName = account.displayName
        val email = account.email

        Log.d("Menu", "Sign-in successful! ID Token: $idToken, Display Name: $displayName, Email: $email")

        // Pass user's name to com.example.pawsome.ProfileActivity
        val profileIntent = Intent(this, ProfileActivity::class.java).apply {
            putExtra("userName", displayName)
        }
        startActivity(profileIntent)

        // Store user data locally (using SharedPreferences)
        val sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("id_token", idToken)
            putString("display_name", displayName)
            putString("email", email)
            apply()
        }

        // Handle potential null case for ID token
        validateIdTokenOnServer(idToken)

        // Navigate to com.example.pawsome.MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Optional: close the com.example.pawsome.MenuActivity
    }

    private fun validateIdTokenOnServer(idToken: String?) {
        if (idToken != null) {
            // Proceed with validation logic using idToken
            // ... your code to send idToken to server ...
        } else {
            // Handle the case where idToken is null (e.g., log an error)
            Log.e("Menu", "validateIdTokenOnServer: ID token is null")
        }
    }
}
