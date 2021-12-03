package io.github.reedkrawiec.buzz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        setSupportActionBar(findViewById(R.id.toolbar_settings))
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }/*
            val home = Intent(this, MainActivity::class.java)
            startActivity(home)
             */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            val from = intent.getStringExtra("from")
            if(from.equals("LandingPage")) {
                val settings = Intent(this, LandingPage::class.java)
                startActivity(settings)
            } else if (from.equals("CodeInput")){
                val codeInput = Intent(this, CodeInput::class.java)
                startActivity(codeInput)
            } else if (from.equals("TimerActivity")){
                val timer = Intent(this,TimerActivity::class.java)
                timer.putExtra("code",intent.getStringExtra("code"))
                startActivity(timer)
            }
            return true;
        }
        return super.onOptionsItemSelected(item)
    }
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}