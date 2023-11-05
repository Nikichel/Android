package com.example.smartalarmclock

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartalarmclock.controlAlarm.ControlAlarm
import com.example.smartalarmclock.database.DbManager
import com.example.smartalarmclock.databinding.ActivityRecycleViewBinding
import com.example.smartalarmclock.extraConstants.extraConstants
import java.util.Calendar

@Suppress("DEPRECATION")
class ShowAlarms : AppCompatActivity(), AlarmClockAdapter.Listener {
    private lateinit var binding: ActivityRecycleViewBinding
    private val alarmAdapter = AlarmClockAdapter(this)
    private lateinit var setAlarmLauncher: ActivityResultLauncher<Intent>
    private lateinit var editAlarmLauncher: ActivityResultLauncher<Intent>
    private val dbManager = DbManager(this)
    private val controlAlarm = ControlAlarm()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecycleViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        controlAlarm.context=this
        initActivity()
        dbManager.open()
        loadFromDb()
        editAlarmLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                val editAlarm = it.data?.getSerializableExtra(extraConstants.EXTRA_EDIT_ALARM) as AlarmClock
                val oldAlarm = it.data?.getSerializableExtra(extraConstants.EXTRA_ALARM) as AlarmClock
                val editPosition = it.data?.getIntExtra(extraConstants.EXTRA_POSITION_ALARM, -1)!!
                dbManager.updateInDbByHashCode(oldAlarm, editAlarm)
                controlAlarm.onAlarm(editAlarm)
                alarmAdapter.updateAlarm(editAlarm, editPosition)
            }
        }
        setAlarmLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                val alarm = it.data?.getSerializableExtra(extraConstants.EXTRA_ALARM) as AlarmClock
                alarmAdapter.addAlarm(alarm)
                dbManager.insertToDb(alarm)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dbManager.close()
    }
    private fun loadFromDb(){
        val listAlarm = dbManager.readDbData()
        for(alarm in listAlarm)
            alarmAdapter.addAlarm(alarm)
    }
    private fun initActivity(){
        binding.apply{
            recyclerView.layoutManager = LinearLayoutManager(this@ShowAlarms)
            recyclerView.adapter = alarmAdapter
            removeAlarmB.setOnClickListener {
                alarmAdapter.removeSelectedAlarms(dbManager, controlAlarm)
                addAlarmB.visibility = View.VISIBLE
                removeAlarmB.visibility = View.GONE
            }
            addAlarmB.setOnClickListener{
                val setIntent = Intent(this@ShowAlarms, AlarmActivity::class.java)
                setIntent.action = extraConstants.STATE_SET
                setAlarmLauncher.launch(setIntent)
            }
        }
    }
    override fun onSwitch(alarm: AlarmClock) {
        controlAlarm.onAlarm(alarm)
        dbManager.updateInDbByHashCode(alarm, alarm)
        Toast.makeText(this, "Будильник на ${alarm.hour}:${alarm.min} устновлен", Toast.LENGTH_LONG).show();
    }

    override fun offSwitch(alarm: AlarmClock) {
        controlAlarm.offAlarm(alarm)
        dbManager.updateInDbByHashCode(alarm, alarm)
    }

    override fun onEdit(alarm: AlarmClock, position:Int) {
        controlAlarm.offAlarm(alarm)

        val editIntent = Intent(this@ShowAlarms, AlarmActivity::class.java)
        editIntent.putExtra(extraConstants.EXTRA_POSITION_ALARM, position)
        editIntent.putExtra(extraConstants.EXTRA_ALARM, alarm)
        editIntent.action = extraConstants.STATE_EDIT
        editAlarmLauncher.launch(editIntent)
    }

    override fun onSelect(alarm: AlarmClock, position: Int) {
        binding.apply{
            addAlarmB.visibility = View.GONE
            removeAlarmB.visibility = View.VISIBLE
        }
        Toast.makeText(this, "Выбран ${alarm.hour}:${alarm.min}.\nПозиция $position", Toast.LENGTH_LONG).show();
    }
}