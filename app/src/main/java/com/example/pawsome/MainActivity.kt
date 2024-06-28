package com.example.pawsome

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Perform other initialization tasks here if needed

        val currentVersion = getCurrentVersion()
        val updateChecker = UpdateChecker(this)
        updateChecker.checkForUpdate(currentVersion)
    }

    private fun getCurrentVersion(): String {
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName ?: getStoredVersion() ?: "1.0.0"
        } catch (e: Exception) {
            e.printStackTrace()
            getStoredVersion() ?: "1.0.0"
        }
    }

    private fun getStoredVersion(): String? {
        // Implement your logic to retrieve the stored version from SharedPreferences or any other storage mechanism
        // For example, assuming you store it in SharedPreferences:
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("stored_version", null)
    }
}
