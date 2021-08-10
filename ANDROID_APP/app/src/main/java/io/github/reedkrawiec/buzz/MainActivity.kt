package io.github.reedkrawiec.buzz

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private var isListening: Boolean = false
    private var recorder: MediaRecorder? = null
    private var handler: Handler = Handler()
    private var total = 0.0
    private var time = 0.0
    private fun startRecorder() {
        val _recorder = MediaRecorder()
        _recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        _recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        _recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        _recorder.setOutputFile("/dev/null");
        try{
            _recorder.prepare()
        } catch(e: IllegalStateException){
            e.printStackTrace()
        }
        _recorder.start()
        recorder = _recorder
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
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.RECORD_AUDIO),1428)
        } else {
            startRecorder()
        }

        val button = findViewById<Button>(R.id.ListenButton)
        button.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                isListening = !isListening
                if(isListening) {
                    button.text = "STOP"
                    total = 0.0
                    time = 0.0
                    val _thread = object: Runnable {
                        override fun run() {
                            if(isListening && recorder != null){
                                val amp = recorder!!.maxAmplitude
                                total += amp
                                time += 1
                                if(amp > ((total / time) + 200)){
                                    Log.d("TAG", "ALERT")
                                }
                            }
                            handler.postDelayed(this, 100)
                        }
                    }
                    handler.postDelayed(_thread, 5000)
                } else {
                    button.text = "LISTEN"
                    handler.removeCallbacksAndMessages(null)
                }
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.bar, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if(item.itemId == R.id.action_settings){
            Log.d("TAG","SETTING");
            val settings = Intent(this, Settings::class.java)
            startActivity(settings)
        }
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}