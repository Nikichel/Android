package com.example.smartalarmclock.alarmClock

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.Serializable
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

data class AlarmClock(val id: UUID, var hour : String, var min : String, var isActive : Boolean, var isSelect : Boolean): Serializable{
    @RequiresApi(Build.VERSION_CODES.O)
    fun convertToLocaleTime(): String {
        val time = LocalTime.of(hour.toInt(), min.toInt())
        val language = Locale.getDefault().language
        val pattern = if (language == "ru") "HH:mm" else "hh:mm a"
        val formatter = DateTimeFormatter.ofPattern(pattern)
        val formattedTime = time.format(formatter)
        return  formattedTime.toString()
    }
}
