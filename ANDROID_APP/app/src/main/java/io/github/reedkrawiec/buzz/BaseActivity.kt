package io.github.reedkrawiec.buzz

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    fun registerSettingsButton(from:String, code:String) {
        val button_hamburger = findViewById<Button>(R.id.hamburger)
        button_hamburger.setOnClickListener{
            val settings = Intent(this, Settings::class.java)
            settings.putExtra("from",from)
            settings.putExtra("code",code)
            startActivity(settings)
        }
    }
    fun dialog(title:String, text:String) {
        val builder: AlertDialog.Builder? = this?.let {
            AlertDialog.Builder(it)
        }

        // 2. Chain together various setter methods to set the dialog characteristics
        builder?.setMessage(text)
            ?.setTitle(title)

        // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
        builder?.create()?.show()
    }
}