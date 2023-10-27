package com.example.smartalarmclock

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartalarmclock.databinding.ActivityRecycleViewBinding
import com.example.smartalarmclock.extraConstants.extraConstants
import java.util.Calendar

class ShowAlarms : AppCompatActivity(), AlarmClockAdapter.Listener {
    private lateinit var binding: ActivityRecycleViewBinding
    private val alarmAdapter = AlarmClockAdapter(this)
    private lateinit var setAlarmLauncher: ActivityResultLauncher<Intent>
    private lateinit var editAlarmLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecycleViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initActivity()
        editAlarmLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                @Suppress("DEPRECATION")
                val editAlarm = it.data?.getSerializableExtra(extraConstants.EXTRA_ALARM) as AlarmClock
                val editPosition = it.data?.getIntExtra(extraConstants.EXTRA_POSITION_ALARM, -1)!!
                onAlarm(editAlarm)
                alarmAdapter.updateAlarm(editAlarm, editPosition)
            }
        }
        setAlarmLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                @Suppress("DEPRECATION")
                alarmAdapter.addAlarm(it.data?.getSerializableExtra(extraConstants.EXTRA_ALARM) as AlarmClock)
            }
        }
    }

    private fun initActivity(){
        binding.apply{
            recyclerView.layoutManager = LinearLayoutManager(this@ShowAlarms)
            recyclerView.adapter = alarmAdapter
            addAlarmB.setOnClickListener{
                val setIntent = Intent(this@ShowAlarms, AlarmActivity::class.java)
                setIntent.action = extraConstants.STATE_SET
                setAlarmLauncher.launch(setIntent)
            }
        }
    }

    private fun onAlarm(alarm: AlarmClock){
        val clock = Calendar.getInstance()
        val requestCode = alarm.hour.toInt()+alarm.min.toInt()
        var alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra(extraConstants.HOUR, alarm.hour.toInt())
            putExtra(extraConstants.MINUTE, alarm.min.toInt())
        }

        val alarmPendingIntent = PendingIntent.getBroadcast(
            this,
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

    private fun offAlarm(alarm: AlarmClock){
        val requestCode = alarm.hour.toInt()+alarm.min.toInt()

        var alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, requestCode,intent, PendingIntent.FLAG_IMMUTABLE)
        }

        alarmManager.cancel(alarmIntent)
    }
    override fun onSwitch(alarm: AlarmClock) {
        onAlarm(alarm)

        Toast.makeText(this, "Будильник на ${alarm.hour}:${alarm.min} устновлен", Toast.LENGTH_LONG).show();
    }

    override fun offSwitch(alarm: AlarmClock) {
        offAlarm(alarm)
    }

    override fun onEdit(alarm: AlarmClock, position:Int) {
        offAlarm(alarm)

        val editIntent = Intent(this@ShowAlarms, AlarmActivity::class.java)
        editIntent.putExtra(extraConstants.EXTRA_POSITION_ALARM, position)
        editIntent.action = extraConstants.STATE_EDIT
        editAlarmLauncher.launch(editIntent)
    }
}