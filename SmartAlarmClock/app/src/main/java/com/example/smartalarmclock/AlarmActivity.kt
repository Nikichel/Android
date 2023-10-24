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
    private val clock = Calendar.getInstance()
    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmIntent: PendingIntent
    private lateinit var binding: ActivityAlarmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAlarm()
        alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0,intent,PendingIntent.FLAG_IMMUTABLE)
        }

    }

    fun onClickSetAlarmClock(view: View) = with(binding){
        /*clock.set(Calendar.HOUR_OF_DAY, binding.alarmClock.hour)
        clock.set(Calendar.MINUTE, binding.alarmClock.minute)
        clock.set(Calendar.SECOND, 0)
        alarmManager?.setExact(
            AlarmManager.RTC_WAKEUP,
            clock.timeInMillis,
            alarmIntent
        )*/
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
    private fun initAlarm(){
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        binding.alarmClock.setIs24HourView(true)
    }
}