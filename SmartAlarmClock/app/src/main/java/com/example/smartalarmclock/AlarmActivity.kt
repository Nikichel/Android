package com.example.smartalarmclock

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.smartalarmclock.databinding.ActivityAlarmBinding
import java.util.Calendar


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
        alarmIntent = Intent(this, AlarmReceiver::class.java).let { alarmIntent ->
            PendingIntent.getBroadcast(this, 0,intent,PendingIntent.FLAG_IMMUTABLE)
        }

    }

    @SuppressLint("MissingPermission")
    fun onClickSetAlarmClock(view: View){
        clock.set(Calendar.HOUR_OF_DAY, binding.alarmClock.hour)
        clock.set(Calendar.MINUTE, binding.alarmClock.minute)
        alarmManager?.setExact(
            AlarmManager.RTC_WAKEUP,
            clock.timeInMillis,
            alarmIntent
        )

        Log.d("MyLog", "Set Alarm")
    }
    private fun initAlarm(){
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        binding.alarmClock.setIs24HourView(true)
    }
}