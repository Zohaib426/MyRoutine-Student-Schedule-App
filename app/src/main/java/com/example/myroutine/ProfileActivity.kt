package com.example.myroutine

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class ProfileActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var navigationView: NavigationView

    lateinit var tvName: TextView
    lateinit var tvEmail: TextView
    lateinit var tvBatch: TextView
    lateinit var tvCNIC: TextView
    lateinit var tvTimetable: TextView

    lateinit var dbHelper: DBHelper
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        dbHelper = DBHelper(this)

        val userId = intent.getIntExtra("user_id", -1)
        if (userId == -1) {
            finish() // Invalid user ID
            return
        }

        user = dbHelper.getUserById(userId)
        if (user == null) {
            finish() // User not found in DB
            return
        }

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navView)

        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Show hamburger icon on toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_edit_profile -> {
                    val intent = Intent(this, EditProfileActivity::class.java)
                    intent.putExtra("user_id", user!!.id)
                    startActivity(intent)
                }
                R.id.nav_add_course -> {
                    val intent = Intent(this, AddCourseActivity::class.java)
                    intent.putExtra("user_id", user!!.id)
                    startActivity(intent)
                }
                R.id.nav_set_timetable -> {
                    val intent = Intent(this, SetTimetableActivity::class.java)
                    intent.putExtra("user_id", user!!.id)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        tvBatch = findViewById(R.id.tvBatch)
        tvCNIC = findViewById(R.id.tvCNIC)
        tvTimetable = findViewById(R.id.tvTimetable)

        tvName.text = "Name: ${user!!.name}"
        tvEmail.text = "Email: ${user!!.email}"
        tvBatch.text = "Batch: ${user!!.batch ?: "Not set"}"
        tvCNIC.text = "CNIC: ${user!!.cnic ?: "Not set"}"

        val timetable = dbHelper.getTimetable(user!!.id)
        tvTimetable.text = if (timetable.isNotEmpty()) timetable else "No timetable set."
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
