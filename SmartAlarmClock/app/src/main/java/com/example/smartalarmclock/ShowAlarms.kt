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
        init()
        editAlarmLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                @Suppress("DEPRECATION")
                val editAlarm = it.data?.getSerializableExtra("alarmClock") as AlarmClock
                val editPosition = it.data?.getIntExtra("positionAlarm", -1)!!
                Log.d("position", "$editPosition")
                onAlarm(editAlarm)
                alarmAdapter.updateAlarm(editAlarm, editPosition)
            }
        }
        setAlarmLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                @Suppress("DEPRECATION")
                alarmAdapter.addAlarm(it.data?.getSerializableExtra("alarmClock") as AlarmClock)
            }
        }
    }

    private fun init(){
        binding.apply{
            recyclerView.layoutManager = LinearLayoutManager(this@ShowAlarms)
            recyclerView.adapter = alarmAdapter
            addAlarmB.setOnClickListener{
                val setIntent = Intent(this@ShowAlarms, AlarmActivity::class.java)
                setIntent.action = "SET"
                setAlarmLauncher.launch(setIntent)
            }
        }
    }

    private fun onAlarm(alarm: AlarmClock){
        val clock = Calendar.getInstance()
        val requestCode = alarm.hour.toInt()+alarm.min.toInt()
        var alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("hour", alarm.hour.toInt())
            putExtra("minute", alarm.min.toInt())
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

    override fun onEdit(alarmClock: AlarmClock, position:Int) {
        offAlarm(alarmClock)

        val editIntent = Intent(this@ShowAlarms, AlarmActivity::class.java)
        editIntent.putExtra("editPosition", position)
        editIntent.action = "EDIT"
        editAlarmLauncher.launch(editIntent)
    }
}