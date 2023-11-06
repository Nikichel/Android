package com.example.smartalarmclock

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartalarmclock.controlAlarm.ControlAlarm
import com.example.smartalarmclock.database.DbManager
import com.example.smartalarmclock.databinding.ClockItemBinding
import java.util.UUID

class AlarmClockAdapter(private val listener : Listener): RecyclerView.Adapter<AlarmClockAdapter.AlarmHolder>() {
    private val alarmList = ArrayList<AlarmClock>()
    class AlarmHolder(item: View) : RecyclerView.ViewHolder(item){
        private val binding = ClockItemBinding.bind(item)
        fun bind(alarmClock: AlarmClock, listener : Listener) =with(binding){
            clockLayout.setBackgroundColor(Color.WHITE)
            switchAlarm.isChecked = alarmClock.isActive
            if(alarmClock.hour.length == 1){
                alarmClock.hour = "0${alarmClock.hour}"
            }
            if(alarmClock.min.length == 1){
                alarmClock.min = "0${alarmClock.min}"
            }
            val fullTime = "${alarmClock.hour}:${alarmClock.min}"
            tvAlarmTime.text = fullTime
            switchAlarm.setOnCheckedChangeListener{_, isChecked ->
                alarmClock.isActive = switchAlarm.isChecked
                if(isChecked)
                    listener.onSwitch(alarmClock)
                else
                    listener.offSwitch(alarmClock)
            }
            itemView.setOnClickListener {
                listener.onEdit(alarmClock, adapterPosition)
            }
            itemView.setOnLongClickListener {
                alarmClock.isSelect = !alarmClock.isSelect
                if(alarmClock.isSelect)
                    clockLayout.setBackgroundColor(Color.LTGRAY)
                else
                    clockLayout.setBackgroundColor(Color.WHITE)
                listener.onSelect(alarmClock, adapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.clock_item, parent, false)
        return AlarmHolder(view)
    }

    override fun getItemCount(): Int {
        return alarmList.size
    }

    override fun onBindViewHolder(holder: AlarmHolder, position: Int) {
        holder.bind(alarmList[position], listener)
    }

    fun addAlarm(alarm: AlarmClock){
        alarmList.add(alarm)

        notifyItemChanged(alarmList.size-1, null)
    }
    fun removeSelectedAlarms(dbManager: DbManager, showAlarms: ShowAlarms) {
        val iterator = alarmList.iterator()
        while (iterator.hasNext()) {
            val alarm = iterator.next()
            if (alarm.isSelect) {
                val position = alarmList.indexOf(alarm)
                iterator.remove()
                showAlarms.offAlarm(alarm)
                dbManager.deleteFromDbByHashCode(alarm)
                notifyItemRemoved(position)
            }
        }
    }
    fun updateAlarm(alarm: AlarmClock, position: Int) {
        alarmList[position] = alarm
        notifyItemChanged(position)
    }
    interface Listener{
        fun onSwitch(alarm: AlarmClock)
        fun offSwitch(alarm: AlarmClock)
        fun onEdit(alarm: AlarmClock, position: Int)
        fun onSelect(alarm: AlarmClock, position: Int)
    }
}