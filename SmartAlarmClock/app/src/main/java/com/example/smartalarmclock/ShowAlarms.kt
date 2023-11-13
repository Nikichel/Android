package com.example.smartalarmclock

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartalarmclock.alarmClock.AlarmClock
import com.example.smartalarmclock.alarmClock.AlarmReceiver
import com.example.smartalarmclock.database.DbManager
import com.example.smartalarmclock.databinding.ActivityRecycleViewBinding
import com.example.smartalarmclock.extraConstants.extraConstants
import java.util.Calendar

@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.O)
class ShowAlarms : AppCompatActivity(), AlarmClockAdapter.Listener {
    private lateinit var binding: ActivityRecycleViewBinding
    private lateinit var setAlarmLauncher: ActivityResultLauncher<Intent>
    private lateinit var editAlarmLauncher: ActivityResultLauncher<Intent>
    private val dbManager = DbManager(this)
    private val alarmAdapter = AlarmClockAdapter(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecycleViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initActivity()
        dbManager.open()
        loadFromDb()
        editAlarmLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                val editAlarm = it.data?.getSerializableExtra(extraConstants.EXTRA_EDIT_ALARM) as AlarmClock
                val oldAlarm = it.data?.getSerializableExtra(extraConstants.EXTRA_ALARM) as AlarmClock
                val editPosition = it.data?.getIntExtra(extraConstants.EXTRA_POSITION_ALARM, -1)!!
                dbManager.updateInDbByHashCode(oldAlarm, editAlarm)
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
                alarmAdapter.removeSelectedAlarms(dbManager, this@ShowAlarms)
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
        onAlarm(alarm)
        dbManager.updateInDbByHashCode(alarm, alarm)
        val time = getString(R.string.set_alarm_clock) + " " + alarm.convertToLocaleTime()
        Toast.makeText(this, time , Toast.LENGTH_LONG).show()
    }

    override fun offSwitch(alarm: AlarmClock) {
        offAlarm(alarm)
        dbManager.updateInDbByHashCode(alarm, alarm)
    }

    override fun onEdit(alarm: AlarmClock, position:Int) {
        offAlarm(alarm)

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
    }

    fun onAlarm(alarm: AlarmClock){

      val clock = Calendar.getInstance()
        val requestCode = alarm.id.hashCode()
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        /*val currentTime = Calendar.getInstance()
        val alarmTime = alarm.hour.toInt()
        if (currentTime.get(Calendar.HOUR_OF_DAY) > alarmTime ||
            (currentTime.get(Calendar.HOUR_OF_DAY) == alarmTime && currentTime.get(Calendar.MINUTE) >= alarm.min.toInt())) {
            clock.add(Calendar.DAY_OF_YEAR, 1)
        }*/

        clock.set(Calendar.HOUR_OF_DAY, alarm.hour.toInt())
        clock.set(Calendar.MINUTE, alarm.min.toInt())
        clock.set(Calendar.SECOND, 0)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            clock.timeInMillis,
            AlarmManager.INTERVAL_DAY, // Set the repeat interval to 7 days for weekly triggering
            alarmIntent
        )

        /*alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            clock.timeInMillis,
            alarmIntent
        )*/
    }

    fun offAlarm(alarm: AlarmClock){

        val requestCode = alarm.id.hashCode()
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        alarmManager.cancel(alarmIntent)
    }
}