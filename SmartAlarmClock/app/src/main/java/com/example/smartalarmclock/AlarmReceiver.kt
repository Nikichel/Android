package com.example.smartalarmclock

import android.annotation.SuppressLint
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
    @Suppress("DEPRECATION")
    @SuppressLint("InvalidWakeLockTag")
    override fun onReceive(context: Context, intent: Intent) {
        /*val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        @Suppress("DEPRECATION") val wakeLockFlags = PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP
        val wakeLock = powerManager.newWakeLock(wakeLockFlags, "MyApp:MyWakeLockTag")
        if (keyguardManager.isKeyguardLocked) {            // Если экран заблокирован, разблокируйте его
            @Suppress("DEPRECATION") val keyguardLock = keyguardManager.newKeyguardLock("MyApp:MyKeyguardLockTag")
            @Suppress("DEPRECATION")
            keyguardLock.disableKeyguard()
        }
        wakeLock.acquire(1*60*1000L *//*10 minutes*//*)

        val newIntent = Intent(context, MainActivity::class.java)
        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(newIntent)*/

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        @Suppress("DEPRECATION") val wakeLockFlags = PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP
        val wakeLock = powerManager.newWakeLock(wakeLockFlags, "MyApp:MyWakeLockTag")
        if (keyguardManager.isKeyguardLocked) {            // Если экран заблокирован, разблокируйте его
            @Suppress("DEPRECATION") val keyguardLock = keyguardManager.newKeyguardLock("MyApp:MyKeyguardLockTag")
            @Suppress("DEPRECATION")
            keyguardLock.disableKeyguard()
        }

        wakeLock.acquire(1*60*1000L)

        val newIntent = Intent(context, MainActivity::class.java)
        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(newIntent)

        wakeLock.release()
    }
}