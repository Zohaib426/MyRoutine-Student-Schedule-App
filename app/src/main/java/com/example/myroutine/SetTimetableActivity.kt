package com.example.myroutine

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class SetTimetableActivity : AppCompatActivity() {

    private lateinit var spinnerCourses: Spinner
    private lateinit var etDay: EditText
    private lateinit var etTime: EditText
    private lateinit var btnSetTimetable: Button
    private lateinit var tvTimetable: TextView

    private lateinit var dbHelper: DBHelper
    private var userId: Int = -1
    private var courses: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_timetable)

        spinnerCourses = findViewById(R.id.spinnerCourses)
        etDay = findViewById(R.id.etDay)
        etTime = findViewById(R.id.etTime)
        btnSetTimetable = findViewById(R.id.btnSetTimetable)
        tvTimetable = findViewById(R.id.tvTimetable)

        dbHelper = DBHelper(this)
        userId = intent.getIntExtra("user_id", -1)

        courses = dbHelper.getCourses(userId)

        if (courses.isEmpty()) {
            Toast.makeText(this, "No courses found. Please add courses first.", Toast.LENGTH_LONG).show()
            btnSetTimetable.isEnabled = false
        } else {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courses)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCourses.adapter = adapter
        }

        etDay.setOnClickListener { showDayPicker() }
        etTime.setOnClickListener { showTimePicker() }

        btnSetTimetable.setOnClickListener {
            val course = spinnerCourses.selectedItem.toString()
            val day = etDay.text.toString().trim()
            val time = etTime.text.toString().trim()

            if (day.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = dbHelper.addTimetable(userId, course, day, time)
            if (result != -1L) {
                Toast.makeText(this, "Timetable set", Toast.LENGTH_SHORT).show()
                etDay.text.clear()
                etTime.text.clear()
                showTimetable()
            } else {
                Toast.makeText(this, "Error saving timetable", Toast.LENGTH_SHORT).show()
            }
        }

        showTimetable()
    }

    private fun showDayPicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                val fullDate = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(cal.time)
                etDay.setText(fullDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
                etTime.setText(formattedTime)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun showTimetable() {
        val data = dbHelper.getTimetable(userId)
        tvTimetable.text = if (data.isEmpty()) "No timetable set." else data
    }
}
