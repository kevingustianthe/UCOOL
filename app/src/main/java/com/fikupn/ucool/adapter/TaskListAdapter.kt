package com.fikupn.ucool.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.RecyclerView
import com.fikupn.ucool.BaseApplication
import com.fikupn.ucool.R
import com.fikupn.ucool.TaskAddUpdateActivity
import com.fikupn.ucool.model.TaskListModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TaskListAdapter(tasklist: List<TaskListModel>, internal var context: Context) : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {
    private var tasklist : List<TaskListModel> = ArrayList()
    private lateinit var tts : TextToSpeech
    private val today = Date()
    private val dtFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private lateinit var notificationManager: NotificationManagerCompat

    init {
        this.tasklist = tasklist
    }

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var taskName: TextView = view.findViewById(R.id.tv_item_task)
        var taskDate: TextView = view.findViewById(R.id.tv_item_date)
        var btnEdit: Button = view.findViewById(R.id.btn_edit)
        var btnPlay: Button = view.findViewById(R.id.btn_play)
        var sisaWaktu: TextView = view.findViewById(R.id.tv_sisa_waktu)
//        var cv_task = view.findViewById<CardView>(R.id.cv_item_task)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val tasks = tasklist[position]
        val taskDateTime = dtFormat.parse(tasks.taskDate) as Date

        val sisaHari = (taskDateTime.time - today.time) / 86400000
        val sisaJam = (taskDateTime.time - today.time) % 86400000 / 3600000
        val sisaMenit = (taskDateTime.time - today.time) % 86400000 % 3600000 / 60000
        val txtSisaWaktu: String

        notificationManager = NotificationManagerCompat.from(context)

        holder.taskName.text = tasks.taskName
        holder.taskDate.text = tasks.taskDate

        if (sisaHari > 0) {
            txtSisaWaktu = "Sisa waktu: $sisaHari hari $sisaJam jam"
            holder.sisaWaktu.text = txtSisaWaktu
        } else if (sisaJam > 0) {
            txtSisaWaktu = "Sisa waktu: $sisaJam jam $sisaMenit menit"
            holder.sisaWaktu.text = txtSisaWaktu

            val builder = NotificationCompat.Builder(context, BaseApplication.CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_ucolo)
                .setContentTitle(tasks.taskName)
                .setContentText(txtSisaWaktu)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            val notification = builder.build()
            notificationManager.notify(1, notification)
        } else if (sisaMenit > 0) {
            txtSisaWaktu = "Sisa waktu: $sisaMenit menit"
            holder.sisaWaktu.text = txtSisaWaktu
        } else {
            txtSisaWaktu = "Tidak ada sisa waktu"
            holder.sisaWaktu.text = txtSisaWaktu
        }

//        holder.cv_task.setOnClickListener {
//            Toast.makeText(context, "Task: " + tasks.taskName + "\nPosition: " + position, Toast.LENGTH_SHORT).show()
//            val intent = Intent(context, TaskAddUpdateActivity::class.java)
//            intent.putExtra("Mode", "E")
//            intent.putExtra("Id", tasks.id)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            context.startActivity(intent)
//        }

        holder.btnEdit.setOnClickListener {
//            Toast.makeText(context, "Position: " + position, Toast.LENGTH_SHORT).show()
            val intent = Intent(context, TaskAddUpdateActivity::class.java)
            intent.putExtra("Mode", "E")
            intent.putExtra("Id", tasks.id)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        holder.btnPlay.setOnClickListener {
            Toast.makeText(context, "Task: " + tasks.taskName, Toast.LENGTH_SHORT).show()
            tts = TextToSpeech(context) {
                if (it == TextToSpeech.SUCCESS) {
                    tts.language = Locale("id", "ID")
                    tts.setSpeechRate(1.0f)
                    tts.speak("Tugas Anda adalah ${tasks.taskName}. $txtSisaWaktu", TextToSpeech.QUEUE_FLUSH, null)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return tasklist.size
    }
}

