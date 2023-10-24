package com.example.smartalarmclock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartalarmclock.databinding.ActivityRecycleViewBinding

class ShowAlarms : AppCompatActivity() {
    private lateinit var binding: ActivityRecycleViewBinding
    private val alarmAdapter = AlarmClockAdapter()
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
}