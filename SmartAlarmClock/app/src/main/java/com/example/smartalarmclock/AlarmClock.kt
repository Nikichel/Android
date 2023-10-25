package com.example.smartalarmclock

import java.io.Serializable

data class AlarmClock(var hour : String, var min : String, var isActive : Boolean): Serializable
