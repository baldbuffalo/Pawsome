package com.example.pawsome

import android.content.Intent
import android.credentials.CredentialManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.pawsome.databinding.MenuActivityBinding
import com.google.android.gms.auth.api.signin.Credential
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.identity.CredentialManager
import com.google.android.gms.identity.account.GoogleAccountCredential

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: MenuActivityBinding
    private lateinit var credentialManager: CredentialManager
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MenuActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        credentialManager = CredentialManager.getInstance(this)

        signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val pendingCredential = credentialManager.getSignInCredentialFromIntent(result.data!!)
                if (pendingCredential != null) {
                    handleSignInResult(pendingCredential)
                } else {
                    Log.e("Menu", "Sign-in cancelled")
                }
            } else {
                Log.e("Menu", "Sign-in failed")
            }
        }

        // Check if user is already signed in with Google
        val account = GoogleAccountCredential.getSyncAccount(this, null)
        if (account != null) {
            goToMainActivity(account.name) // Use account name for now (modify for other data)
        }

        binding.signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = credentialManager.getSignInIntent(
            CredentialPickerConfig.Builder()
                .setServerClientId(getString(R.string.your_server_client_id)) // Replace with your ID
                .build()
        )
        signInLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(credential: Credential) {
        // Extract user information from credential object (can include display name, email)
        val name = credential.accountDisplayName
        val email = credential.accountEmail

        Log.d("Menu", "Sign-in successful! Name: $name, Email: $email")

        // ... handle user data and potentially request access tokens ...

        goToMainActivity(name) // Pass user name for now (modify for other data)
    }

    private fun goToMainActivity(userName: String) {
        val profileIntent = Intent(this, ProfileActivity::class.java).apply {
            putExtra("userName", userName)
        }
        startActivity(profileIntent)

        // Store user data locally (using SharedPreferences)
        // ... implement using SharedPreferences (modify for user data) ...

        // Validate ID token on server (if applicable)
        // ... implement server validation logic (modify for your setup) ...

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Optional: close MenuActivity
    }
}