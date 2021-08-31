package io.github.reedkrawiec.buzz

import android.app.Notification
import android.app.PendingIntent
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
import androidx.preference.PreferenceManager
import java.io.File
import java.net.URL
import kotlin.concurrent.thread



class MainActivity : AppCompatActivity() {
    private var isListening: Boolean = false
    private var recorder: MediaRecorder? = null
    private var handler: Handler = Handler()
    private var total = 0.0
    private var time = 0.0
    private var last_notif_time = 0.0
    private var temp_path: String? = null
    private fun startRecorder() {

        Log.d("TAG","YEP")

        val _recorder = MediaRecorder()
        _recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        _recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        _recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        val files = getExternalMediaDirs()
        temp_path = files[0].absolutePath + "/test2.3gp"
        _recorder.setOutputFile(temp_path)

        try{
            _recorder.prepare()
            _recorder.start()
            Log.d("TAG","started")
            recorder = _recorder

        } catch(e: IllegalStateException){
            e.printStackTrace()
        }

    }
    override fun onDestroy(){
        Log.d("TAG","DESTROYING")
        File(temp_path).delete()
        recorder!!.stop()
        recorder!!.release()
        super.onDestroy()

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("TAG", grantResults.toString())
        startRecorder()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.RECORD_AUDIO),1428)
        } else if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && recorder == null){
            Log.d("Tag",(ContextCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED).toString())
            startRecorder()
        }
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val button = findViewById<Button>(R.id.ListenButton)
        button.setOnClickListener {
              if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                isListening = !isListening
                if(isListening) {
                    button.text = "STOP"
                    total = 0.0
                    time = 0.0
                    last_notif_time = 0.0
                    val _thread = object: Runnable {
                        override fun run() {
                            if(isListening && recorder != null){
                                val amp = recorder!!.maxAmplitude
                                total += amp
                                time += 1
                                if(amp > ((total / time) + 200) && (last_notif_time == 0.0 || (time -  last_notif_time) > 300)){
                                    thread(start = true) {
                                        last_notif_time = time

                                        val ip = prefs.getString("serverip","default")
                                        if(ip != null){
                                            Log.d("TAG", ip)
                                            //"http://6ddc59dd55b2.ngrok.io/"
                                            val url = URL(ip).readText()
                                        }

                                    }

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
            handler.removeCallbacksAndMessages(null)
            if(recorder != null){
                recorder!!.stop()
                recorder!!.release()
                recorder = null
            }
            val settings = Intent(this, Settings::class.java)
            startActivity(settings)
        }
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}