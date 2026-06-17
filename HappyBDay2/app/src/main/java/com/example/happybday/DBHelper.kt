package com.example.happybday

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "Birthdays.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE birthdays (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, message TEXT, mediaUri TEXT)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS birthdays")
        onCreate(db)
    }

    fun addBirthday(b: Birthday): Long {
        val db = this.writableDatabase
        val cv = ContentValues().apply {
            put("name", b.name)
            put("phone", b.phone)
            put("month", b.month)
            put("day", b.day)
            put("hour", b.hour)
            put("minute", b.minute)
            put("message", b.message)
            put("mediaUri", b.mediaUri) // Salvăm adresa pozei
        }
        return db.insert("birthdays", null, cv)
    }

    fun updateBirthday(b: Birthday): Int {
        val db = this.writableDatabase
        val cv = ContentValues().apply {
            put("name", b.name)
            put("phone", b.phone)
            put("month", b.month)
            put("day", b.day)
            put("hour", b.hour)
            put("minute", b.minute)
            put("message", b.message)
            put("mediaUri", b.mediaUri) // Actualizăm adresa pozei
        }
        return db.update("birthdays", cv, "id = ?", arrayOf(b.id.toString()))
    }

    fun getBirthdaysByMonth(month: Int): List<Birthday> {
        val list = mutableListOf<Birthday>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM birthdays WHERE month = ?", arrayOf(month.toString()))
        if (cursor.moveToFirst()) {
            do {
                list.add(Birthday(
                    cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getInt(3), cursor.getInt(4), cursor.getInt(5),
                    cursor.getInt(6), cursor.getString(7),
                    cursor.getString(8) ?: "" // Citim adresa pozei
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}