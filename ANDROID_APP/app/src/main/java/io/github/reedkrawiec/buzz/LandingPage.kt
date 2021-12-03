package io.github.reedkrawiec.buzz


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import java.net.URL
import kotlin.concurrent.thread

class LandingPage : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)
        registerSettingsButton("LandingPage","")
        val button = findViewById<Button>(R.id.landingPageButton)

        button.setOnClickListener {
            val codeInput = Intent(this, CodeInput::class.java)
            startActivity(codeInput)
        }
    }

}