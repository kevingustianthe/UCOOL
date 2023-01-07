package com.fikupn.ucool

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fikupn.ucool.database.DBTaskHelper
import com.fikupn.ucool.databinding.ActivityTaskAddUpdateBinding
import com.fikupn.ucool.model.TaskListModel
import java.text.SimpleDateFormat
import java.util.*

class TaskAddUpdateActivity : AppCompatActivity(){
    private val cal = Calendar.getInstance()
    private lateinit var binding: ActivityTaskAddUpdateBinding

    private var dbTaskHelper : DBTaskHelper ?= null
    private var isEdit : Boolean = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        dbTaskHelper = DBTaskHelper(this)

        //Date Time
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dtFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        if (intent != null && intent.getStringExtra("Mode") == "E") {
            isEdit = true
            supportActionBar?.title = "Ubah"
            binding.btnSubmit.text = "Update"
            binding.btnDelete.visibility = View.VISIBLE

            val tasks : TaskListModel = dbTaskHelper!!.getTask(intent.getIntExtra("Id", 0))
            val taskDate = tasks.taskDate.split(" ")[0]
            val taskTime = tasks.taskDate.split(" ")[1]
            binding.edtTask.setText(tasks.taskName)
            binding.selectDate.text = taskDate
            binding.selectTime.text = taskTime
            cal.time = dtFormat.parse(tasks.taskDate) as Date

            //Date Picker
            binding.selectDate.setOnClickListener {
                val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                    cal.set(Calendar.DAY_OF_MONTH, selectedDay)
                    cal.set(Calendar.MONTH, selectedMonth)
                    cal.set(Calendar.YEAR, selectedYear)
                    binding.selectDate.text = dateFormat.format(cal.time)
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                datePickerDialog.show()
            }
            //Time Picker
            binding.selectTime.setOnClickListener {
                val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                    cal.set(Calendar.HOUR_OF_DAY, selectedHour)
                    cal.set(Calendar.MINUTE, selectedMinute)
                    binding.selectTime.text = timeFormat.format(cal.time)
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
                timePickerDialog.show()
            }

        } else {
            isEdit = false
            supportActionBar?.title = "Tambah"
            binding.btnSubmit.text = "Simpan"
            binding.btnDelete.visibility = View.GONE

            //Date Picker
            binding.selectDate.setOnClickListener {
                val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                    cal.set(Calendar.DAY_OF_MONTH, selectedDay)
                    cal.set(Calendar.MONTH, selectedMonth)
                    cal.set(Calendar.YEAR, selectedYear)
                    binding.selectDate.text = dateFormat.format(cal.time)
                }, year, month, day)

                datePickerDialog.datePicker.minDate = cal.timeInMillis
                datePickerDialog.show()
            }
            //Time Picker
            binding.selectTime.setOnClickListener {
                val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                    cal.set(Calendar.HOUR_OF_DAY, selectedHour)
                    cal.set(Calendar.MINUTE, selectedMinute)
                    binding.selectTime.text = timeFormat.format(cal.time)
                }, hour, minute, true)
                timePickerDialog.show()
            }
        }

        binding.btnSubmit.setOnClickListener {
            var success = false
            val tasks = TaskListModel()
            if (isEdit) {
                // Update
                tasks.id = intent.getIntExtra("Id", 0)
                tasks.taskName = binding.edtTask.text.toString()
                tasks.taskDate = dtFormat.format(cal.time)

                success = dbTaskHelper?.updateTask(tasks) as Boolean
            } else {
                // Add
                tasks.taskName = binding.edtTask.text.toString()
                tasks.taskDate = dtFormat.format(cal.time)

                success = dbTaskHelper?.addTask(tasks) as Boolean
            }

            if (success) {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                if (isEdit) {
                    Toast.makeText(applicationContext, "Gagal Update", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Gagal Tambah", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            val taskId = intent.getIntExtra("Id", 0)
            val dialog = AlertDialog.Builder(this).setTitle("Info").setMessage("Click Yes If You Want to Delete the Task")
                .setPositiveButton("Yes") { dialog, _ ->
                    val success = dbTaskHelper?.deleteTask(taskId) as Boolean
                    if (success) {
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            dialog.show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}