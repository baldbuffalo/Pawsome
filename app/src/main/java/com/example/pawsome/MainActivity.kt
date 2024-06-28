package com.example.pawsome

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
            packageInfo.versionName ?: "1.0.0" // Use "1.0.0" as fallback if versionName is null
        } catch (e: Exception) {
            e.printStackTrace()
            "1.0.0" // Fallback version
        }
    }
}
