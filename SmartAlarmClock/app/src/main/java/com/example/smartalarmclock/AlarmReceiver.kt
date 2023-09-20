package com.example.smartalarmclock

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.PowerManager
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService

class AlarmReceiver : BroadcastReceiver() {
    private lateinit var wakeLock: PowerManager.WakeLock
    private var ringtone: Ringtone? = null
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("MyLog", "ALARM!")


        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        @Suppress("DEPRECATION") val wakeLockFlags = PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP
        val wakeLock = powerManager.newWakeLock(wakeLockFlags, "MyApp:MyWakeLockTag")
        if (keyguardManager.isKeyguardLocked) {            // Если экран заблокирован, разблокируйте его
            @Suppress("DEPRECATION") val keyguardLock = keyguardManager.newKeyguardLock("MyApp:MyKeyguardLockTag")
            @Suppress("DEPRECATION")
            keyguardLock.disableKeyguard()
        }
        // Получите Uri звукового ресурса (например, из ресурсов приложения)
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        // Создайте объект Ringtone с использованием RingtoneManager
        val ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
        // Воспроизведите рингтон
        ringtone?.play()
        // Захватите WakeLock, чтобы предотвратить блокировку экрана во время воспроизведения
        wakeLock.acquire()


        val newIntent = Intent(context, MainActivity::class.java)
        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(newIntent)
    }



}