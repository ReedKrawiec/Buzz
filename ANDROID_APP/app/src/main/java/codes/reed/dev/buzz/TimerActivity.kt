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
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.net.URL
import kotlin.concurrent.thread

val MAX_AMP = 24_000.0

class TimerActivity : BaseActivity(), OnSeekBarChangeListener {
    private var recorder: MediaRecorder? = null
    // Path to where we're storing our temp output
    private var tempPath: String? = null
    private var threadHandler: Handler = Handler()
    // The threshold for how loud the sound needs
    // to be to trigger an alert. Start at 1.0 to
    // prevent unexpected alerts
    private var tolerancePercentage = 1.0
    private var isListening = false
    private var time = 0.0
    private var lastNotifTime = 0.0
    private lateinit var outputFile: File
    private val timeIntervalsBetweenAlerts:Long = 300
    private val timeInitialDelay:Long = 5000
    private val timeIntervalLength:Long = 100
    private fun startRecorder() {
        val _recorder = MediaRecorder()
        _recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        _recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        _recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        val files = getExternalMediaDirs()
        // We need to pipe the data for the recorder somewhere, and /dev/null isn't accepted
        // as an output anymore, so we pipe the output to a temp file that we periodically
        tempPath = files[0].absolutePath + "/trash.3gp"
        outputFile = File(tempPath)
        _recorder.setOutputFile(tempPath)
        try {
            _recorder.prepare()
            _recorder.start()
            recorder = _recorder

        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

    }
    override fun onDestroy(){
        // We need to do these things before
        // terminating so we can safely terminate.
        File(tempPath).delete()
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
        //Once we've received permission to read
        //audio, start the MediaRecorder
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        startRecorder()
    }
    override fun onBackPressed() {
        // On back, default to returning to the code input screen
        val codeInput = Intent(this, CodeInput::class.java)
        codeInput.putExtra("code",intent.getStringExtra("code"))
        startActivity(codeInput)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        // The only way to navigate to this activity is through
        // CodeInput, and code is guaranteed to be filled.
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
            // If we already have recording permissions, start the MediaRecorder
            startRecorder()
        }

        val toleranceElement = findViewById<TextView>(R.id.tolerance)
        toleranceElement.setOnClickListener{
            dialog("Help:",resources.getString(R.string.tolerance_help))
        }
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val thresholdSeek = findViewById<SeekBar>(R.id.seekBar)
        thresholdSeek.setOnSeekBarChangeListener(this)

        val button = findViewById<Button>(R.id.centerButton)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val ip = prefs.getString("serverip","https://buzz.reed.codes")

        button.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                isListening = !isListening
                if(isListening) {
                    button.text = "Listening\nfor alert!"
                    time = 0.0
                    lastNotifTime = 0.0
                    val _thread = object: Runnable {
                        override fun run() {
                            if(isListening && recorder != null){
                                val amp = recorder!!.maxAmplitude
                                // Overwrite anything in the outputFile already,
                                // this prevents the file size from bloating
                                // over the recording period.
                                outputFile.writeText("")
                                val percentage = (amp/MAX_AMP)
                                progressBar.progress = (percentage * 100).toInt()
                                time += 1
                                // We check that both the percentage is valid to send an alert,
                                // and that an alert wasn't already sent within a period of time.
                                if(percentage > tolerancePercentage && (lastNotifTime == 0.0 || (time -  lastNotifTime) > timeIntervalsBetweenAlerts)){
                                    thread(start = true) {
                                        lastNotifTime = time
                                        try{
                                            URL("$ip/alert/$code").readText()
                                        } catch(e: Exception) {
                                            Snackbar.make(findViewById(R.id.centerButton), resources.getString(R.string.alert_failure), Snackbar.LENGTH_INDEFINITE)
                                                .show()
                                        }

                                    }
                                }
                            }
                            // This creates a loop that will continuously execute,
                            // every 100 milliseconds
                            threadHandler.postDelayed(this, timeIntervalLength)
                        }
                    }
                    threadHandler.postDelayed(_thread, timeInitialDelay)
                } else {
                    button.text = "Click to\nListen!"
                    threadHandler.removeCallbacksAndMessages(null)
                }
            }
        }


    }
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        //When we drag the seekbar, change our internal tolerance
        tolerancePercentage = (p1/100.0).toDouble()
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }
}