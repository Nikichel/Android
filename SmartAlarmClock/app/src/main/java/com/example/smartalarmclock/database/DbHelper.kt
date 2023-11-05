package com.example.smartalarmclock.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) :SQLiteOpenHelper(context, DbAlarmClass.DATABASE_NAME, null, DbAlarmClass.DATABASE_VERSION)  {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(DbAlarmClass.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DbAlarmClass.SQL_DELETE_TABLE)
        onCreate(db)
    }
}