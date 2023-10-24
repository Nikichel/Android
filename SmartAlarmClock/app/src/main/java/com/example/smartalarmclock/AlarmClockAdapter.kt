package com.example.smartalarmclock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartalarmclock.databinding.ClockItemBinding

class AlarmClockAdapter(private val listener : Listener): RecyclerView.Adapter<AlarmClockAdapter.AlarmHolder>() {
    private val alarmList = ArrayList<AlarmClock>()
    class AlarmHolder(item: View) : RecyclerView.ViewHolder(item){
        private val binding = ClockItemBinding.bind(item)
        fun bind(alarmClock: AlarmClock, listener : Listener) =with(binding){
            val fullTime = "${alarmClock.hour}:${alarmClock.min}"
            alarmTime.text = fullTime
            alarmSwitch.setOnCheckedChangeListener{_, isChecked ->
                if(isChecked){
                    listener.onSwitch(alarmClock)
                }
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
        notifyDataSetChanged()
    }
    interface Listener{
        fun onSwitch(alarm: AlarmClock)
    }
}