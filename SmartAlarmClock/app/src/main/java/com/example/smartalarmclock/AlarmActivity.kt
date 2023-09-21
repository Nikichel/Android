package com.example.smartalarmclock

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartalarmclock.databinding.ActivityAlarmBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmBinding
    private val sdt = SimpleDateFormat("HH:mm", Locale.getDefault())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.alarmClock.setIs24HourView(true)

        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }
    }

    fun onClickSetAlarmClock(view: View){
        val clock = Calendar.getInstance()
        clock.set(Calendar.SECOND, 0)
        clock.set(Calendar.MINUTE, binding.alarmClock.minute)
        clock.set(Calendar.HOUR_OF_DAY, binding.alarmClock.hour)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var alarmClockInfo = AlarmManager.AlarmClockInfo(clock.timeInMillis, getAlarmClockInfoPendingIntent())

        alarmManager.setAlarmClock(alarmClockInfo, getAlarmActionPendingIntent())
        Toast.makeText(this, "Будильник " + sdt.format(clock.time), Toast.LENGTH_LONG).show()
    }

    private fun getAlarmClockInfoPendingIntent(): PendingIntent{
        val alarmClockIntent = Intent(this, AlarmActivity::class.java)
        alarmClockIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(this, 0, alarmClockIntent,PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getAlarmActionPendingIntent(): PendingIntent{
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}