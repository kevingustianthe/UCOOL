package com.fikupn.ucool

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fikupn.ucool.adapter.TaskListAdapter
import com.fikupn.ucool.database.DBTaskHelper
import com.fikupn.ucool.databinding.ActivityMainBinding
import com.fikupn.ucool.helper.Constant
import com.fikupn.ucool.helper.PreferenceHelper
import com.fikupn.ucool.model.TaskListModel
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var taskListAdapter: TaskListAdapter ?= null
    private var dbTaskHandler : DBTaskHelper ?= null
    private var taskList : List<TaskListModel> = ArrayList()
    private var linearLayoutManager : LinearLayoutManager ?= null
    private lateinit var sharedPref: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = PreferenceHelper(this)
        supportActionBar?.hide()

        binding.toolbar.title = "UCOOL"

        dbTaskHandler = DBTaskHelper(this)

        val toggle = ActionBarDrawerToggle(
            this, binding.drwLayout, binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drwLayout.addDrawerListener(toggle)
        toggle.syncState()

        //Dropdown
        val navHeaderDrawer = binding.navView.getHeaderView(0)
        val tvNama = navHeaderDrawer.findViewById<TextView>(R.id.tv_name)
        val tvEmail = navHeaderDrawer.findViewById<TextView>(R.id.tv_email)

        tvNama.text = sharedPref.getString(Constant.PREF_NAMA)
        tvEmail.text = sharedPref.getString(Constant.PREF_EMAIL)

        val navDrawerMenu = NavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_logout -> {
                    //Toast.makeText(this, "Log Out", Toast.LENGTH_SHORT).show()
                    alertClick()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

        binding.navView.setNavigationItemSelectedListener(navDrawerMenu)

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, TaskAddUpdateActivity::class.java)
            startActivity(intent)
        }

        fetchlist()
    }

    private fun fetchlist() {
        taskList = dbTaskHandler!!.getAllTask()
        taskListAdapter = TaskListAdapter(taskList, applicationContext)
        linearLayoutManager = LinearLayoutManager(applicationContext)

        binding.rvTask.layoutManager = linearLayoutManager
        binding.rvTask.adapter = taskListAdapter
    }

    private fun alertClick() {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null)
        val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
        val btnLogoutOkay = mDialogView.findViewById<Button>(R.id.btn_logoutOkay)
        val btnLogoutCancel = mDialogView.findViewById<Button>(R.id.btn_logoutCancel)
        val  mAlertDialog = mBuilder.show()

        btnLogoutCancel.setOnClickListener {
            mAlertDialog.dismiss()
            Toast.makeText(this, "Belum Log Out", Toast.LENGTH_SHORT).show()
        }
        btnLogoutOkay.setOnClickListener{
            sharedPref.clear()
            Toast.makeText(this, "Log Out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LogIn::class.java))
            finish()
        }
    }
}