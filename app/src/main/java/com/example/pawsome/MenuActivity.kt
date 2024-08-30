package com.example.pawsome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.pawsome.databinding.MenuActivityBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.common.api.ApiException

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: MenuActivityBinding
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MenuActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the Google Sign-In client
        oneTapClient = Identity.getSignInClient(this)

        signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    handleSignInResult(credential)
                } catch (e: ApiException) {
                    Log.e("Menu", "Sign-in failed", e)
                }
            } else {
                Log.e("Menu", "Sign-in canceled or failed")
            }
        }

        // Check if user is already signed in
        val account = getCurrentSignedInAccount()
        if (account != null) {
            goToMainActivity(account.displayName) // Modify for other data if needed
        }

        binding.signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.your_server_client_id)) // Replace with your ID
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                signInLauncher.launch(result.pendingIntent.intent)
            }
            .addOnFailureListener { e ->
                Log.e("Menu", "Google Sign-In failed", e)
            }
    }

    private fun handleSignInResult(credential: SignInCredential) {
        val name = credential.displayName
        val email = credential.id

        Log.d("Menu", "Sign-in successful! Name: $name, Email: $email")

        goToMainActivity(name ?: "") // Handle nullability of name
    }

    private fun getCurrentSignedInAccount(): SignInCredential? {
        // Logic to get currently signed-in account, if any.
        // This might involve checking stored credentials or querying the Identity API.
        return null // Replace with actual logic if needed
    }

    private fun goToMainActivity(userName: String) {
        val profileIntent = Intent(this, ProfileActivity::class.java).apply {
            putExtra("userName", userName)
        }
        startActivity(profileIntent)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Optional: close MenuActivity
    }
}
