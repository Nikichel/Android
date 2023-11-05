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

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val hour = intent.getIntExtra(extraConstants.HOUR, 0)
        val minute = intent.getIntExtra(extraConstants.MINUTE, 0)

        setNewAlarm(hour, minute, context)

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        @Suppress("DEPRECATION") val wakeLockFlags = PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP
        val wakeLock = powerManager.newWakeLock(wakeLockFlags, "MyApp:MyWakeLockTag")
        if (keyguardManager.isKeyguardLocked) {            // Если экран заблокирован, разблокируйте его
            @Suppress("DEPRECATION") val keyguardLock = keyguardManager.newKeyguardLock("MyApp:MyKeyguardLockTag")
            @Suppress("DEPRECATION")
            keyguardLock.disableKeyguard()
        }
        wakeLock.acquire(10*60*1000L /*10 minutes*/)

        val newIntent = Intent(context, MainActivity::class.java)
        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(newIntent)
    }

    private fun setNewAlarm(hour: Int, minute: Int, context: Context){
        val clock = Calendar.getInstance()
        val requestCode = hour+minute
        var alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(extraConstants.HOUR, hour)
            putExtra(extraConstants.MINUTE, minute)
        }

        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        clock.add(Calendar.DAY_OF_MONTH, 1)
        clock.set(Calendar.HOUR_OF_DAY, hour)
        clock.set(Calendar.MINUTE,  minute)
        clock.set(Calendar.SECOND, 0)
        alarmManager?.setExact(
            AlarmManager.RTC_WAKEUP,
            clock.timeInMillis,
            alarmPendingIntent
        )

    }
}