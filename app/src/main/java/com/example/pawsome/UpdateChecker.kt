package com.example.pawsome

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.*
import java.io.IOException

data class GitHubRelease(
    @SerializedName("tag_name") val tagName: String,
    val assets: List<Asset>
)

data class Asset(
    @SerializedName("browser_download_url") val browserDownloadUrl: String
)

class UpdateChecker(private val context: Context) {
    private val client = OkHttpClient()
    private val gson = Gson()

    private val url: String by lazy {
        context.getString(R.string.github_repo_url)
    }

    fun checkForUpdate(currentVersion: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                showToast("Failed to check for updates")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body
                val responseCode = response.code

                if (response.isSuccessful) {
                    try {
                        val bodyString = responseBody.string()
                        val release = gson.fromJson(bodyString, GitHubRelease::class.java)
                        val latestVersion = release.tagName

                        if (isVersionNewer(latestVersion, currentVersion)) {
                            (context as Activity).runOnUiThread {
                                showUpdateDialog(release.assets[0].browserDownloadUrl)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showToast("Error parsing update data")
                    } finally {
                        responseBody.close()
                    }
                } else {
                    showToast("Failed to get update data: $responseCode")
                }
            }
        })
    }

    private fun isVersionNewer(newVersion: String, currentVersion: String): Boolean {
        return newVersion > currentVersion
    }

    private fun showUpdateDialog(downloadUrl: String) {
        (context as? Activity)?.runOnUiThread {
            MaterialDialog(context).show {
                title(text = "Update Available")
                message(text = "A new version of the app is available. Would you like to update?")
                positiveButton(text = "Update") {
                    downloadAndInstallUpdate(downloadUrl)
                }
                negativeButton(text = "Cancel")
            }
        }
    }

    private fun downloadAndInstallUpdate(downloadUrl: String) {
        val request = DownloadManager.Request(Uri.parse(downloadUrl))
            .setTitle("App Update")
            .setDescription("Downloading update...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "app-update.apk")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        showToast("Downloading update...")
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
