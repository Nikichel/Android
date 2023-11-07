package com.example.smartalarmclock.alarmClock

import java.io.Serializable
import java.util.UUID

data class AlarmClock(val id: UUID, var hour : String, var min : String, var isActive : Boolean, var isSelect : Boolean): Serializable
