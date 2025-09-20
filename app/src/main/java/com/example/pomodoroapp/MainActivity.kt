package com.example.pomodoroapp

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PowerManager
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.pomodoroapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var timer: CountDownTimer? = null

    private var timeRemaining: Long = 0
    private var studyDurationMinute: Long = 25
    private var breakDurationMinute: Long = 5

    private var studyStatus = true // true for study, false for break
    private var timerStatus: Boolean = true // true for running, false for paused

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var wakeLock: PowerManager.WakeLock



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)


        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "PomodoroApp::WakeLockTag")
        wakeLock.acquire()



        if (binding.btnBreak.text == "Go Back Study") {
            studyStatus = false
        } else {
            studyStatus = true
        }

        val sharedPreferences = getSharedPreferences("PomodoroPreferences", Context.MODE_PRIVATE)
        studyDurationMinute = sharedPreferences.getLong("PomodoroDurationMinute", 25)
        breakDurationMinute = sharedPreferences.getLong("BreakDurationMinute", 5)


        startTimer(studyDurationMinute)

        buttonFunctions()

    }




    override fun onResume() {
        super.onResume()
        timerStatus = true



        if (!wakeLock.isHeld) {
            wakeLock.acquire()
        }


        // Reload preferences when app resumes
        val sharedPreferences = getSharedPreferences("PomodoroPreferences", Context.MODE_PRIVATE)
        studyDurationMinute = sharedPreferences.getLong("PomodoroDurationMinute", 25)
        breakDurationMinute = sharedPreferences.getLong("BreakDurationMinute", 5)


        if(SettingsStatus.settingsStatus){
            val durationToReset = if (studyStatus) studyDurationMinute else breakDurationMinute
            resetTimer(durationToReset)
        }else{
            val sharedPreferences = getSharedPreferences("PomodoroPreferences", Context.MODE_PRIVATE)
            studyDurationMinute = sharedPreferences.getLong("PomodoroDurationMinute", 25)
            breakDurationMinute = sharedPreferences.getLong("BreakDurationMinute", 5)

            binding.countdowm.setTextColor(ContextCompat.getColor(this, R.color.dark_red))
            timerStatus = false
            continueTimer()
            timer?.cancel()
        }

    }

    override fun onStop() {
        super.onStop()
        SettingsStatus.settingsStatus = false



        val sharedPreferences = getSharedPreferences("PomodoroPreferences", Context.MODE_PRIVATE)
        studyDurationMinute = sharedPreferences.getLong("PomodoroDurationMinute", 25)
        breakDurationMinute = sharedPreferences.getLong("BreakDurationMinute", 5)


        timer?.cancel()
        timerStatus = false

        if (wakeLock.isHeld) {
            wakeLock.release()
        }

    }



    private fun startTimer(durationMinute: Long) {
        if (binding.btnBreak.text == "Go Back Study") {
            studyStatus = false
        } else {
            studyStatus = true
        }


        timer = object : CountDownTimer((durationMinute * 60000) + 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.prepare()
                }

                timeRemaining = millisUntilFinished
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                binding.countdowm.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                mediaPlayer.start()
                timerStatus = false
            }
        }.start()
    }

    private fun resetTimer(durationMinute: Long) {
        timer?.cancel()
        startTimer(durationMinute)
        binding.countdowm.setTextColor(ContextCompat.getColor(this, R.color.white))
        timerStatus = true
    }

    private fun buttonFunctions() {
        binding.countdowm.setOnClickListener {
            if (!timerStatus) {
                continueTimer()
                binding.countdowm.setTextColor(ContextCompat.getColor(this, R.color.white))
                timerStatus = true
            } else {
                timer?.cancel()
                binding.countdowm.setTextColor(ContextCompat.getColor(this, R.color.dark_red))
                timerStatus = false
            }
        }

        binding.settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            timer?.cancel()
            timerStatus = false
        }

        binding.btnReset.setOnClickListener {
            val durationToReset = if (studyStatus) studyDurationMinute else breakDurationMinute
            resetTimer(durationToReset)
            timerStatus = true
        }

        binding.btnBreak.setOnClickListener {
            if (binding.btnBreak.text == "Go Back Study" && !studyStatus) {
                resetTimer(studyDurationMinute)
                studyStatus = true
                binding.btnBreak.text = "Break"
            } else {
                resetTimer(breakDurationMinute)
                studyStatus = false
                binding.btnBreak.text = "Go Back Study"
            }
        }
    }

    private fun continueTimer() {
        if (binding.btnBreak.text == "Go Back Study" && !studyStatus) {
            studyStatus = false
        } else {
            studyStatus = true
        }


        timer = object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.prepare()
                }

                timeRemaining = millisUntilFinished
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                binding.countdowm.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                timerStatus = false
                mediaPlayer.start()
            }
        }.start()
    }
}

