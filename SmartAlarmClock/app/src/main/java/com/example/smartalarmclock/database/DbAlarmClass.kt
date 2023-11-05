package com.example.smartalarmclock.database

import android.provider.BaseColumns

object DbAlarmClass {
    const val TABLE_NAME = "alarms"
    const val COLUMN_HOUR = "hour"
    const val COLUMN_MIN = "minute"
    const val COLUMN_IS_ACTIVE = "active"
    const val COLUMN_UUID = "uuid"

    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "dbAlarms.db"

    const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "$COLUMN_HOUR INTEGER,$COLUMN_MIN INTEGER,$COLUMN_IS_ACTIVE INTEGER,$COLUMN_UUID TEXT)"

    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
}