package com.example.myroutine

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddCourseActivity : AppCompatActivity() {

    private lateinit var etCourseName: EditText
    private lateinit var btnAddCourse: Button
    private lateinit var dbHelper: DBHelper
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)

        etCourseName = findViewById(R.id.etCourseName)
        btnAddCourse = findViewById(R.id.btnAddCourse)
        dbHelper = DBHelper(this)

        userId = intent.getIntExtra("user_id", -1)

        btnAddCourse.setOnClickListener {
            val courseName = etCourseName.text.toString().trim()
            if (courseName.isEmpty()) {
                Toast.makeText(this, "Please enter a course name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = dbHelper.addCourse(userId, courseName)
            if (result != -1L) {
                Toast.makeText(this, "Course added", Toast.LENGTH_SHORT).show()
                etCourseName.text.clear()
            } else {
                Toast.makeText(this, "Failed to add course", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
