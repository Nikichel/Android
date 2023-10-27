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
import com.example.smartalarmclock.extraConstants.extraConstants
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
        val action = intent.action
        var hour = alarmClock.hour.toString()
        var min = alarmClock.minute.toString()

        if(hour.length == 1){
            hour = "0$hour"
        }
        if(min.length == 1){
            min = "0$min"
        }
        val alarmClock = AlarmClock(hour, min, false)
        if(action == extraConstants.STATE_SET) {
            val setIntent = Intent().apply {
                putExtra(extraConstants.EXTRA_ALARM, alarmClock)
            }
            setResult(RESULT_OK, setIntent)
        }
        else if(action == extraConstants.STATE_EDIT){
            alarmClock.isActive = true
            val position = intent.getIntExtra(extraConstants.EXTRA_POSITION_ALARM, -1)
            val editIntent = Intent().apply {
                putExtra(extraConstants.EXTRA_ALARM, alarmClock)
                putExtra(extraConstants.EXTRA_POSITION_ALARM, position)
            }
            setResult(RESULT_OK, editIntent)
        }
        finish()
    }
}