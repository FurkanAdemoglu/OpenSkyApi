package com.example.openskyapicase

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.openskyapicase.util.extension.isInternetAvailable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        if (this.isInternetAvailable()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle("Bağlantı Hatası")
                .setMessage("İnternet bağlantısı bulunamadı. Lütfen bağlantınızı kontrol edin.")
                .setCancelable(false)
                .setPositiveButton("Tekrar Dene") { _, _ -> recreate() }
                .setNegativeButton("Çıkış") { _, _ -> finish() }
                .show()
        }
        super.onCreate(savedInstanceState)
    }
}