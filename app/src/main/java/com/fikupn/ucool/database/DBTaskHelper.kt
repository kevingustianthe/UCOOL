package com.fikupn.ucool.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.fikupn.ucool.model.TaskListModel

class DBTaskHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        private const val DB_NAME = "dbucoolapp"
        private const val DB_VERSION = 1
        private const val TABLE_NAME = "task_list"
        private const val TASK_ID = "task_id"
        private const val TASK_NAME = "task_name"
        private const val TASK_DATE = "task_date"
    }

    override fun onCreate(sqldb: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME ($TASK_ID INTEGER PRIMARY KEY, $TASK_NAME TEXT, $TASK_DATE TEXT);"
        sqldb?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(sqldb: SQLiteDatabase?, p1: Int, p2: Int) {
        val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
        sqldb?.execSQL(DROP_TABLE)
        onCreate(sqldb)
    }

    @SuppressLint("Range")
    fun getAllTask(): List<TaskListModel> {
        val tasklist = ArrayList<TaskListModel>()
        val db = writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME ORDER BY $TASK_DATE ASC"
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val tasks = TaskListModel()
                    tasks.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(TASK_ID)))
                    tasks.taskName = cursor.getString(cursor.getColumnIndex(TASK_NAME))
                    tasks.taskDate = cursor.getString(cursor.getColumnIndex(TASK_DATE))
                    tasklist.add(tasks)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return tasklist
    }

    // Insert data
    fun addTask(tasks: TaskListModel) : Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(TASK_NAME, tasks.taskName)
        values.put(TASK_DATE, tasks.taskDate)

        val _success = db.insert(TABLE_NAME, null, values)
        db.close()

        return (Integer.parseInt("$_success") != -1)
    }

    // Select data
    @SuppressLint("Range")
    fun getTask(_id: Int) : TaskListModel {
        val tasks = TaskListModel()
        val db = writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $TASK_ID = $_id"
        val cursor = db.rawQuery(selectQuery, null)

        cursor?.moveToFirst()
        tasks.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(TASK_ID)))
        tasks.taskName = cursor.getString(cursor.getColumnIndex(TASK_NAME))
        tasks.taskDate = cursor.getString(cursor.getColumnIndex(TASK_DATE))

        cursor.close()
        return tasks
    }

    // Delete data
    fun deleteTask(_id: Int) : Boolean {
        val db = this.writableDatabase
        val _success = db.delete(TABLE_NAME, TASK_ID + "=?", arrayOf(_id.toString())).toLong()

        db.close()
        return (Integer.parseInt("$_success") != -1)
    }

    // Update data
    fun updateTask(tasks: TaskListModel) : Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(TASK_NAME, tasks.taskName)
        values.put(TASK_DATE, tasks.taskDate)
        val _success = db.update(TABLE_NAME, values, TASK_ID + "=?", arrayOf(tasks.id.toString())).toLong()

        db.close()
        return (Integer.parseInt("$_success") != -1)
    }
}