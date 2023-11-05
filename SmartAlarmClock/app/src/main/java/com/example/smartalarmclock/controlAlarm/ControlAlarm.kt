package com.example.smartalarmclock.controlAlarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.smartalarmclock.AlarmClock
import com.example.smartalarmclock.AlarmReceiver
import com.example.smartalarmclock.extraConstants.extraConstants
import java.util.Calendar

class ControlAlarm (){
    var context: Context? = null
    fun onAlarm(alarm: AlarmClock){
        val clock = Calendar.getInstance()
        val requestCode = alarm.id.hashCode()
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(extraConstants.EXTRA_ALARM, alarm)
        }

        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        clock.set(Calendar.HOUR_OF_DAY, alarm.hour.toInt())
        clock.set(Calendar.MINUTE, alarm.min.toInt())
        clock.set(Calendar.SECOND, 0)
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            clock.timeInMillis,
            alarmPendingIntent
        )
    }

    fun offAlarm(alarm: AlarmClock){
        val requestCode = alarm.id.hashCode()

        var alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, requestCode,intent, PendingIntent.FLAG_IMMUTABLE)
        }

        alarmManager.cancel(alarmIntent)
    }
}