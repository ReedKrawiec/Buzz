package codes.reed.dev.buzz

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.OnBackPressedCallback
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.net.URL
import kotlin.concurrent.thread

val MAX_AMP = 24_000.0

class TimerActivity : BaseActivity(), OnSeekBarChangeListener {
    private var recorder: MediaRecorder? = null
    private var temp_path: String? = null
    private var handler: Handler = Handler()
    private var tolerancePercentage = 0.5
    private var isListening = false
    private var total = 0.0
    private var time = 0.0
    private var last_notif_time = 0.0
    private lateinit var outputFile: File;
    private fun startRecorder() {
        val _recorder = MediaRecorder()
        _recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        _recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        _recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        val files = getExternalMediaDirs()
        // We need to pipe the data for the recorder somewhere, and /dev/null isn't accepted
        // as an output anymore, so we pipe the output to a temp file that we periodically
        temp_path = files[0].absolutePath + "/trash.3gp"
        outputFile = File(temp_path)
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
    override fun onBackPressed() {
        val codeInput = Intent(this, CodeInput::class.java)
        codeInput.putExtra("code",intent.getStringExtra("code"))
        startActivity(codeInput)
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
        val ip = prefs.getString("serverip","https://buzz.reed.codes")

        button.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                isListening = !isListening
                if(isListening) {
                    button.text = "Listening\nfor alert!"
                    time = 0.0
                    last_notif_time = 0.0
                    val _thread = object: Runnable {
                        override fun run() {
                            if(isListening && recorder != null){
                                val amp = recorder!!.maxAmplitude
                                outputFile.writeText("")
                                val percentage = (amp/MAX_AMP)
                                progressBar.progress = (percentage * 100).toInt()
                                time += 1
                                if(amp/MAX_AMP > tolerancePercentage && (last_notif_time == 0.0 || (time -  last_notif_time) > 300)){
                                    thread(start = true) {
                                        last_notif_time = time
                                        try{
                                            URL("$ip/alert/$code").readText()
                                        } catch(e: Exception) {
                                            Snackbar.make(findViewById(R.id.centerButton), resources.getString(R.string.alert_failure), Snackbar.LENGTH_INDEFINITE)
                                                .show()
                                        }

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
        tolerancePercentage = (p1/100.0).toDouble()
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }
}