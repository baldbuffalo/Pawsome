package com.example.pawsome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.pawsome.databinding.MenuActivityBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: MenuActivityBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MenuActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // This app only needs the signed-in user's basic profile/email.
        // Requesting an ID token here was unnecessary because the app does not
        // send the token to a backend, and it can trigger an oauth2:openid token
        // request that fails with BAD_AUTHENTICATION on some devices/accounts.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                Log.w("Menu", "Sign-in canceled or failed: resultCode=${result.resultCode}")
                return@registerForActivityResult
            }

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                handleSignInResult(account)
            } catch (e: ApiException) {
                Log.e("Menu", "Sign-in failed: statusCode=${e.statusCode}", e)
            } catch (e: Exception) {
                Log.e("Menu", "Unexpected error while completing Google sign-in", e)
            }
        }

        // Check whether a valid Google account is already signed in.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            goToMainActivity(account.displayName.orEmpty())
        }

        binding.signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        signInLauncher.launch(googleSignInClient.signInIntent)
    }

    private fun handleSignInResult(account: GoogleSignInAccount?) {
        if (account == null) {
            Log.e("Menu", "Google sign-in returned no account")
            return
        }

        Log.d("Menu", "Sign-in successful! Name: ${account.displayName}, Email: ${account.email}")
        goToMainActivity(account.displayName.orEmpty())
    }

    private fun goToMainActivity(userName: String) {
        startActivity(Intent(this, ProfileActivity::class.java).apply {
            putExtra("userName", userName)
        })

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
