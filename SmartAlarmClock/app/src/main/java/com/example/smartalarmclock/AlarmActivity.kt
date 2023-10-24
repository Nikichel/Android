package com.example.smartalarmclock

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartalarmclock.databinding.ActivityAlarmBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.alarmClock.setIs24HourView(true)
    }

    fun onClickSetAlarmClock(view: View) = with(binding){
        var hour = alarmClock.hour.toString()
        var min = alarmClock.minute.toString()

        if(hour.length == 1){
            hour = "0$hour"
        }
        if(min.length == 1){
            min = "0$min"
        }
        val alarmClock = AlarmClock(hour, min)
        val setIntent = Intent().apply{
            putExtra("alarmClock", alarmClock)
        }
        setResult(RESULT_OK, setIntent)
        finish()
    }
}