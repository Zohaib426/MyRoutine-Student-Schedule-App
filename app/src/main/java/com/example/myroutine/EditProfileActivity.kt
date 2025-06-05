package com.example.myroutine

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {

    lateinit var etCNIC: EditText
    lateinit var etBatch: EditText
    lateinit var btnSave: Button

    lateinit var dbHelper: DBHelper
    var userId: Int = -1  // You'll pass this from login or session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        etCNIC = findViewById(R.id.etCNIC)
        etBatch = findViewById(R.id.etBatch)
        btnSave = findViewById(R.id.btnSave)

        dbHelper = DBHelper(this)

        // For this example, set a default userId
        userId = intent.getIntExtra("user_id", -1)

        val user = dbHelper.getUserById(userId)
        if (user != null) {
            etCNIC.setText(user.cnic ?: "")
            etBatch.setText(user.batch ?: "")
        }

        btnSave.setOnClickListener {
            val cnic = etCNIC.text.toString().trim()
            val batch = etBatch.text.toString().trim()

            if (cnic.isEmpty() || batch.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = dbHelper.updateProfile(userId, cnic, batch)
            if (result > 0) {
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
