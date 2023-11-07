package com.example.smartalarmclock.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.smartalarmclock.alarmClock.AlarmClock
import java.util.UUID

class DbManager(context: Context) {
    private val dbHelper = DbHelper(context)

    private var db: SQLiteDatabase? = null

    fun open(){
        db = dbHelper.writableDatabase
    }
    fun insertToDb(alarmClock: AlarmClock){
        val values = ContentValues().apply {
            put(DbAlarmClass.COLUMN_HOUR, alarmClock.hour.toInt())
            put(DbAlarmClass.COLUMN_MIN, alarmClock.min.toInt())
            put(DbAlarmClass.COLUMN_IS_ACTIVE, alarmClock.isActive)
            put(DbAlarmClass.COLUMN_UUID, alarmClock.id.toString())
        }

        db?.insert(DbAlarmClass.TABLE_NAME, null, values)
    }
    fun readDbData(): ArrayList<AlarmClock>{
        val alarmClockList = ArrayList<AlarmClock>()

        val cursor = db?.query(DbAlarmClass.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null)

        with(cursor){
            while(this?.moveToNext()!!){
                val hour = this.getInt(this.getColumnIndex(DbAlarmClass.COLUMN_HOUR))
                val min = this.getInt(this.getColumnIndex(DbAlarmClass.COLUMN_MIN))
                val isActrive = this.getInt(this.getColumnIndex(DbAlarmClass.COLUMN_IS_ACTIVE)) !=0
                val id = this.getString(this.getColumnIndex(DbAlarmClass.COLUMN_UUID))
                val alarmClock = AlarmClock(UUID.fromString(id),hour.toString(), min.toString(), isActrive,false)
                alarmClockList.add(alarmClock)
            }
        }
        cursor?.close()
        return alarmClockList
    }

    fun close(){
        dbHelper.close()
    }
    fun updateInDbByHashCode(oldAlarmClock: AlarmClock, newAlarmClock: AlarmClock) {
        val values = ContentValues().apply {
            put(DbAlarmClass.COLUMN_HOUR, newAlarmClock.hour.toInt())
            put(DbAlarmClass.COLUMN_MIN, newAlarmClock.min.toInt())
            put(DbAlarmClass.COLUMN_IS_ACTIVE, newAlarmClock.isActive)
            put(DbAlarmClass.COLUMN_UUID, newAlarmClock.id.toString())
        }

        val selection = "${DbAlarmClass.COLUMN_UUID} = ?"
        val selectionArgs = arrayOf(oldAlarmClock.id.toString())

        db?.update(DbAlarmClass.TABLE_NAME, values, selection, selectionArgs)
    }

    fun deleteFromDbByHashCode(alarmClock: AlarmClock) {
        val selection = "${DbAlarmClass.COLUMN_UUID} = ?"
        val selectionArgs = arrayOf(alarmClock.id.toString())

        db?.delete(DbAlarmClass.TABLE_NAME, selection, selectionArgs)
    }
}