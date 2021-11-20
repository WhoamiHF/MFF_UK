package com.example.mffuk

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        val btnSchedule: Button = findViewById (R.id.btnSchedule)

        btnSchedule.setOnClickListener() {
            val intent = Intent(this, Schedule::class.java)
            startActivity(intent)
        }
    }
}