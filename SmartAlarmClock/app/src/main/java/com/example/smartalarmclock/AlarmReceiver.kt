package com.example.smartalarmclock

import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.example.smartalarmclock.extraConstants.extraConstants
import java.util.Calendar

@Suppress("DEPRECATION")
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val alarm = intent.getSerializableExtra(extraConstants.EXTRA_ALARM) as AlarmClock

        setNewAlarm(alarm, context)

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val wakeLockFlags = PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP
        val wakeLock = powerManager.newWakeLock(wakeLockFlags, "MyApp:MyWakeLockTag")
        if (keyguardManager.isKeyguardLocked) {            // Если экран заблокирован, разблокируйте его
            val keyguardLock = keyguardManager.newKeyguardLock("MyApp:MyKeyguardLockTag")
            keyguardLock.disableKeyguard()
        }
        wakeLock.acquire(10*60*1000L /*10 minutes*/)

        val newIntent = Intent(context, MainActivity::class.java)
        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(newIntent)
    }

    private fun setNewAlarm(alarm: AlarmClock, context: Context){
        val clock = Calendar.getInstance()
        val requestCode = alarm.id.hashCode()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(extraConstants.EXTRA_ALARM, alarm)
        }

        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        clock.add(Calendar.DAY_OF_MONTH, 1)
        clock.set(Calendar.HOUR_OF_DAY, alarm.hour.toInt())
        clock.set(Calendar.MINUTE,  alarm.min.toInt())
        clock.set(Calendar.SECOND, 0)
        alarmManager?.setExact(
            AlarmManager.RTC_WAKEUP,
            clock.timeInMillis,
            alarmPendingIntent
        )

    }
}