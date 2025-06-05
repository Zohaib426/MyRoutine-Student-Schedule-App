package com.example.myroutine

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "StudentApp.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // User Table
        db.execSQL("""
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                email TEXT UNIQUE,
                password TEXT,
                cnic TEXT,
                batch TEXT
            )
        """.trimIndent())

        // Course Table
        db.execSQL("""
            CREATE TABLE courses (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                course_name TEXT
            )
        """.trimIndent())

        // Timetable Table
        db.execSQL("""
            CREATE TABLE timetable (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                course_name TEXT,
                day TEXT,
                time TEXT
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS courses")
        db.execSQL("DROP TABLE IF EXISTS timetable")
        onCreate(db)
    }

    // Register user
    fun registerUser(name: String, email: String, password: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("email", email)
            put("password", password)
        }
        return db.insert("users", null, values)
    }

    // Login user
    fun loginUser(email: String, password: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id FROM users WHERE email = ? AND password = ?",
            arrayOf(email, password)
        )
        return if (cursor.moveToFirst()) {
            val userId = cursor.getInt(0)
            cursor.close()
            userId
        } else {
            cursor.close()
            -1
        }
    }

    // Update user profile
    fun updateProfile(userId: Int, cnic: String, batch: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("cnic", cnic)
            put("batch", batch)
        }
        return db.update("users", values, "id = ?", arrayOf(userId.toString()))
    }

    // Get logged-in user info
    fun getUserById(userId: Int): User? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE id = ?", arrayOf(userId.toString()))
        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getInt(0),
                name = cursor.getString(1),
                email = cursor.getString(2),
                password = cursor.getString(3),
                cnic = cursor.getString(4),
                batch = cursor.getString(5)
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    // Save timetable
    fun addTimetable(userId: Int, courseName: String, day: String, time: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("user_id", userId)
            put("course_name", courseName)
            put("day", day)
            put("time", time)
        }
        return db.insert("timetable", null, values)
    }

    // Get timetable as string
    fun getTimetable(userId: Int): String {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT course_name, day, time FROM timetable WHERE user_id = ?", arrayOf(userId.toString()))
        val builder = StringBuilder()
        while (cursor.moveToNext()) {
            builder.append("Course: ${cursor.getString(0)}, ${cursor.getString(1)} at ${cursor.getString(2)}\n")
        }
        cursor.close()
        return builder.toString()
    }

    // Add course
    fun addCourse(userId: Int, courseName: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("user_id", userId)
            put("course_name", courseName)
        }
        return db.insert("courses", null, values)
    }

    // Get user courses
    fun getCourses(userId: Int): List<String> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT course_name FROM courses WHERE user_id = ?", arrayOf(userId.toString()))
        val list = mutableListOf<String>()
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0))
        }
        cursor.close()
        return list
    }
}