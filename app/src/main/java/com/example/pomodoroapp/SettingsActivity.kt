package com.example.pomodoroapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pomodoroapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding
    lateinit var editor: SharedPreferences.Editor




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val powerManager = getSystemService(POWER_SERVICE) as PowerManager


        // Initialize SharedPreferences
        val sharedPreferences = getSharedPreferences("PomodoroPreferences", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // Load saved preferences
        val savedPomodoroDuration = sharedPreferences.getLong("PomodoroDurationMinute", 25)
        val savedBreakDuration = sharedPreferences.getLong("BreakDurationMinute", 5)


        // Set the loaded values to the TextViews
        binding.studyMinute.text = savedPomodoroDuration.toString()
        binding.breakMinute.text = savedBreakDuration.toString()



        binding.btnBack.setOnClickListener {
            SettingsStatus.settingsStatus = true
            editor.putLong("PomodoroDurationMinute", binding.studyMinute.text.toString().toLong())
            editor.putLong("BreakDurationMinute", binding.breakMinute.text.toString().toLong())
            editor.apply()
            finish()
        }

        binding.cvPomodoro.setOnClickListener {
            showPomodoroAdjustmentDialog()
        }

        binding.cvBreak.setOnClickListener{
            showBreakAdjustmentDialog()
        }



    }



    override fun onPause() {
        super.onPause()
        SettingsStatus.settingsStatus = true
        // Save the current values to SharedPreferences
        editor.putLong("PomodoroDurationMinute", binding.studyMinute.text.toString().toLong())
        editor.putLong("BreakDurationMinute", binding.breakMinute.text.toString().toLong())
        editor.apply()

        Log.d("SettingsActivity", "OnPause called")
    }

    private fun showBreakAdjustmentDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.break_timer_change_dialog)

        // Set the background to transparent
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Adjust the layout parameters to make the dialog bigger
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialog.window?.setGravity(Gravity.CENTER)


        val btnIncrease = dialog.findViewById<Button>(R.id.bd_btnIncrease)
        val btnDecrease = dialog.findViewById<Button>(R.id.bd_btnDecrease)
        val btnDone = dialog.findViewById<Button>(R.id.bd_btnDone)

        val tvBreak = dialog.findViewById<TextView>(R.id.bpBreak)

        // Set current value to the dialog
        tvBreak.text = binding.breakMinute.text


        btnIncrease.setOnClickListener {
            tvBreak.text = (tvBreak.text.toString().toInt() + 1).toString()
            binding.breakMinute.text = tvBreak.text
        }

        btnDecrease.setOnClickListener {
            if (tvBreak.text.toString().toInt() > 1){
                tvBreak.text = (tvBreak.text.toString().toInt() - 1).toString()
                binding.breakMinute.text = tvBreak.text
            }
        }

        btnDone.setOnClickListener {


            dialog.dismiss()
        }

        dialog.show()

    }

    private fun showPomodoroAdjustmentDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.pomodoro_timer_change_dialog)

        // Set the background to transparent
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Adjust the layout parameters to make the dialog bigger
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialog.window?.setGravity(Gravity.CENTER)

        val btnIncrease = dialog.findViewById<Button>(R.id.pd_btnIncrease)
        val btnDecrease = dialog.findViewById<Button>(R.id.pd_btnDecrease)
        val btnDone = dialog.findViewById<Button>(R.id.pd_btnDone)

        val tvPomodoro = dialog.findViewById<TextView>(R.id.pdPomodoro)

        // Set current value to the dialog
        tvPomodoro.text = binding.studyMinute.text

        btnIncrease.setOnClickListener {
            tvPomodoro.text = (tvPomodoro.text.toString().toInt() + 1).toString()
            binding.studyMinute.text = tvPomodoro.text
        }

        btnDecrease.setOnClickListener {
            if (tvPomodoro.text.toString().toInt() > 1){
                tvPomodoro.text = (tvPomodoro.text.toString().toInt() - 1).toString()
                binding.studyMinute.text = tvPomodoro.text
            }

        }

        btnDone.setOnClickListener {

            dialog.dismiss()
        }

        dialog.show()

    }


}
