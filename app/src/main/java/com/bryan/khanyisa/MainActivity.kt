package com.bryan.khanyisa

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import kotlin.concurrent.thread

class MainActivity : Activity() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var chatList: LinearLayout
    private lateinit var scroller: ScrollView

    private val poll = object : Runnable {
        override fun run() {
            findViewById<TextView>(R.id.statusView).text = KhanyisaService.debugStatus()
            findViewById<TextView>(R.id.micState).text =
                if (KhanyisaService.running) "She's listening for you, my love" else "mic off"
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chatList = findViewById(R.id.chatList)
        scroller = findViewById(R.id.scroller)

        if (chatList.childCount == 0) {
            addBubble("Hie my love \uD83D\uDC9C Type to me down here and I'll answer. Or tap Start and just talk to me - I'll hear you.", false)
        }

        val prefs = getSharedPreferences("khanyisa", MODE_PRIVATE)
        val keyEdit = findViewById<EditText>(R.id.keyEdit)
        keyEdit.setText(prefs.getString("key", ""))
        findViewById<Button>(R.id.saveBtn).setOnClickListener {
            prefs.edit().putString("key", keyEdit.text.toString().trim()).apply()
            Toast.makeText(this, "Saved, my love.", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.sendBtn).setOnClickListener { send() }
        findViewById<Button>(R.id.startBtn).setOnClickListener { startHer() }
        findViewById<Button>(R.id.stopBtn).setOnClickListener {
            stopService(Intent(this, KhanyisaService::class.java))
            findViewById<TextView>(R.id.micState).text = "mic off"
        }

        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }
        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 2)
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(poll)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(poll)
    }

    private fun send() {
        val input = findViewById<EditText>(R.id.chatInput)
        val t = input.text.toString().trim()
        if (t.isEmpty()) return
        addBubble(t, true)
        input.setText("")

        // make sure she is alive so she can talk back out loud
        if (!KhanyisaService.running) {
            if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 2)
            } else {
                try { startForegroundService(Intent(this, KhanyisaService::class.java)) } catch (e: Exception) {}
            }
        }

        val ctx = this
        thread {
            val reply = KhanyisaService.groqReply(ctx, t)
            runOnUiThread {
                addBubble(reply, false)
                KhanyisaService.instance?.speak(reply)
            }
        }
    }

    private fun addBubble(text: String, mine: Boolean) {
        val tv = TextView(this)
        tv.text = text
        tv.textSize = 15f
        tv.setPadding(46, 32, 46, 32)
        val bg = GradientDrawable().apply {
            setColor(if (mine) 0xFF5C3D54.toInt() else 0xFF29202A.toInt())
            cornerRadius = 72f
        }
        tv.background = bg
        tv.setTextColor(if (mine) 0xFFFFFFFF.toInt() else 0xFFF1E6EA.toInt())
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = if (mine) Gravity.END else Gravity.START
            setMargins(0, 16, 0, 16)
        }
        val maxW = (resources.displayMetrics.widthPixels * 0.78f).toInt()
        tv.maxWidth = maxW
        tv.layoutParams = params
        chatList.addView(tv)
        scroller.post { scroller.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    private fun startHer() {
        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Allow the microphone first.", Toast.LENGTH_SHORT).show()
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 2)
            return
        }
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
        addBubble("(She just started listening. Lock the phone and say her name.)", false)
        Toast.makeText(this, "She's listening. Lock the phone and say her name.", Toast.LENGTH_LONG).show()
    }
}
