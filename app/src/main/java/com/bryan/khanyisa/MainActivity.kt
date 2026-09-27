package com.bryan.khanyisa

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("khanyisa", MODE_PRIVATE)
        val keyEdit = findViewById<EditText>(R.id.keyEdit)
        keyEdit.setText(prefs.getString("key", ""))

        findViewById<Button>(R.id.saveBtn).setOnClickListener {
            prefs.edit().putString("key", keyEdit.text.toString().trim()).apply()
            Toast.makeText(this, "Saved. She has her key now.", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.startBtn).setOnClickListener { startHer() }
        findViewById<Button>(R.id.stopBtn).setOnClickListener {
            stopService(Intent(this, KhanyisaService::class.java))
        }

        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 2)
        }
    }

    private fun startHer() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Allow the microphone first.", Toast.LENGTH_SHORT).show()
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 2)
            return
        }
        // ask the phone never to put her to sleep
        val pm = getSystemService(PowerManager::class.java)
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            try {
                startActivity(
                    Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                        Uri.parse("package:$packageName"))
                )
            } catch (e: Exception) { }
        }
        startForegroundService(Intent(this, KhanyisaService::class.java))
        Toast.makeText(this, "She's listening. Lock the phone and say her name.", Toast.LENGTH_LONG).show()
    }
}
