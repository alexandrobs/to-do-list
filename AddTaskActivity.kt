package com.example.todolist.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.databinding.ActivityAddTaskBinding
import com.example.todolist.datasource.TaskDataSource
import com.example.todolist.extensions.format
import com.example.todolist.extensions.text
import com.example.todolist.model.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*
import kotlin.concurrent.timerTask

class AddTaskActivity : AppCompatActivity () {

    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra(TASK_ID)){
            val intExtra = intent.getIntExtra(TASK_ID, 0)
            TaskDataSource.findById(intExtra)?.let {
                binding.tilTitle.text = it.title
                binding.tilDate.text = it.date
                binding.tilHour.text = it.hour
            }
        }

        insertListeners()
    }

    private fun insertListeners(){
        binding.tilDate.editText?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()

            datePicker.addOnPositiveButtonClickListener {
                val default = TimeZone.getDefault()
                val offset = default.getOffset(Date().time) * -1
                binding.tilDate.text = Date(it + offset).format()
            }
            datePicker.show(supportFragmentManager, "DATE_PICK_TAG")
        }

        binding.tilHour.editText?.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .build()

            timePicker.addOnPositiveButtonClickListener {
                val hour = if(timePicker.hour in 0..9) "0${timePicker.hour}" else timePicker.hour
                val minute = if(timePicker.minute in 0..9) "0${timePicker.minute}" else timePicker.minute
                binding.tilHour.text = "$hour:$minute"
            }
            timePicker.show(supportFragmentManager, "TIMER_PICK_TAG")
        }

        binding.mbCancelTask.setOnClickListener {
            finish()
        }

        binding.mbCreateTask.setOnClickListener {
            val task = Task(
                title = binding.tilTitle.text,
                date = binding.tilDate.text,
                hour = binding.tilHour.text,
                id = intent.getIntExtra(TASK_ID, 0)
            )
            TaskDataSource.insertTask(task)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    companion object {
        const val TASK_ID = "task_id"
    }

}