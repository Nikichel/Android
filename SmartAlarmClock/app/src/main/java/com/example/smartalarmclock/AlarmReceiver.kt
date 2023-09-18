package com.example.smartalarmclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("MyLog", "ALARM!")

        val newIntent = Intent(context, MainActivity::class.java)
        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(newIntent)
    }
}