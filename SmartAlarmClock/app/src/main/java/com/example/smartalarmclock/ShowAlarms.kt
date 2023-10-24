package com.example.smartalarmclock

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecycleViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
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
                setAlarmLauncher.launch(Intent(this@ShowAlarms, AlarmActivity::class.java))
            }
        }
    }

    private fun initAlarmManager(alarm: AlarmClock){
        val clock = Calendar.getInstance()
        var alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0,intent, PendingIntent.FLAG_IMMUTABLE)
        }

        clock.set(Calendar.HOUR_OF_DAY, alarm.hour.toInt())
        clock.set(Calendar.MINUTE, alarm.min.toInt())
        clock.set(Calendar.SECOND, 0)
        alarmManager?.setExact(
            AlarmManager.RTC_WAKEUP,
            clock.timeInMillis,
            alarmIntent
        )
    }
    override fun onSwitch(alarm: AlarmClock) {

        initAlarmManager(alarm)

        Toast.makeText(this, "Будильник на ${alarm.hour}:${alarm.min} устновлен", Toast.LENGTH_LONG).show();
    }
}