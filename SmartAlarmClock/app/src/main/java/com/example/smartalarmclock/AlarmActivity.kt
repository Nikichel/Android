@file:Suppress("DEPRECATION")

package com.example.smartalarmclock

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.smartalarmclock.alarmClock.AlarmClock
import com.example.smartalarmclock.databinding.ActivityAlarmBinding
import com.example.smartalarmclock.extraConstants.extraConstants
import java.util.UUID


class AlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.alarmClock.setIs24HourView(true)
    }

    fun onClickSetAlarmClock(view: View) = with(binding){
        val action = intent.action
        val hour = alarmClock.hour.toString()
        val min = alarmClock.minute.toString()

        val alarmClock = AlarmClock(UUID.randomUUID(), hour, min, isActive = false, isSelect = false)
        if(action == extraConstants.STATE_SET) {
            val setIntent = Intent().apply {
                putExtra(extraConstants.EXTRA_ALARM, alarmClock)
            }
            setResult(RESULT_OK, setIntent)
        }
        else if(action == extraConstants.STATE_EDIT){
            alarmClock.isActive = false
            val oldAlarm = intent.getSerializableExtra(extraConstants.EXTRA_ALARM) as AlarmClock
            val position = intent.getIntExtra(extraConstants.EXTRA_POSITION_ALARM, -1)
            val editIntent = Intent().apply {
                putExtra(extraConstants.EXTRA_EDIT_ALARM, alarmClock)
                putExtra(extraConstants.EXTRA_ALARM, oldAlarm)
                putExtra(extraConstants.EXTRA_POSITION_ALARM, position)
            }
            setResult(RESULT_OK, editIntent)
        }
        finish()
    }
}