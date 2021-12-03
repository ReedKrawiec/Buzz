package io.github.reedkrawiec.buzz

import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import android.widget.SeekBar.OnSeekBarChangeListener
import java.io.File
import java.net.URL
import kotlin.concurrent.thread

class TimerActivity : BaseActivity(), OnSeekBarChangeListener {
    private var recorder: MediaRecorder? = null
    private var temp_path: String? = null
    private var handler: Handler = Handler()
    private var tolerance = 0
    private var isListening = false
    private var total = 0.0
    private var time = 0.0
    private var last_notif_time = 0.0
    private fun startRecorder() {
        val _recorder = MediaRecorder()
        _recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        _recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        _recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        val files = getExternalMediaDirs()
        temp_path = files[0].absolutePath + "/test2.3gp"
        if(File(temp_path).exists()){
            File(temp_path).delete()
        }
        _recorder.setOutputFile(temp_path)
        try {
            _recorder.prepare()
            _recorder.start()
            recorder = _recorder

        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

    }
    override fun onDestroy(){
        File(temp_path).delete()
        if(isListening){
            recorder!!.stop()
        }
        recorder!!.release()
        super.onDestroy()

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        startRecorder()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        val code = intent.getStringExtra("code")!!
        registerSettingsButton("TimerActivity",code)
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                1428
            )
        } else if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED && recorder == null
        ) {
            startRecorder()
        }
        val toleranceElement = findViewById<TextView>(R.id.tolerance)
        toleranceElement.setOnClickListener{
            dialog("Help:",resources.getString(R.string.tolerance_help))
        }
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val ThresholdSeek = findViewById<SeekBar>(R.id.seekBar)
        ThresholdSeek.setOnSeekBarChangeListener(this)
        val button = findViewById<Button>(R.id.centerButton)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val ip = prefs.getString("serverip","default")
        Thread {
            URL(ip + "/pair/" + intent.getStringExtra("code")!!).readText()
        }.start()
        button.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                isListening = !isListening
                if(isListening) {
                    button.text = "Listening\nfor alert!"
                    total = 0.0
                    time = 0.0
                    last_notif_time = 0.0
                    val _thread = object: Runnable {
                        override fun run() {
                            if(isListening && recorder != null){
                                val amp = recorder!!.maxAmplitude
                                progressBar.progress = ((amp/36000.0) * 100).toInt()
                                total += amp
                                time += 1
                                if(amp > ((total / time) + tolerance) && (last_notif_time == 0.0 || (time -  last_notif_time) > 300)){
                                    thread(start = true) {
                                        last_notif_time = time
                                        val url = URL(ip + "/alert/" + code).readText()
                                    }

                                }
                            }
                            handler.postDelayed(this, 100)
                        }
                    }
                    handler.postDelayed(_thread, 5000)
                } else {
                    button.text = "Click to\nListen!"
                    handler.removeCallbacksAndMessages(null)
                }
            }
        }
    }
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        tolerance = (200 + (p1/100.0) * 32000).toInt()
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }
}