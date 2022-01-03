package codes.reed.dev.buzz

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import java.net.URL

class CodeInput : BaseActivity() {
    private var isListening: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_input)
        registerSettingsButton("CodeInput","")
        val button = findViewById<Button>(R.id.enterCodeButton)
        val input = findViewById<EditText>(R.id.codeInput)
        val codeHelp = findViewById<TextView>(R.id.codeHelp)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        codeHelp.setOnClickListener{
            dialog("help:",resources.getString(R.string.code_help))
        }
        button.setOnClickListener{
            val ip = prefs.getString("serverip","https://buzz.reed.codes")
            Thread {
                try{
                    val code = input.text.toString()
                    if(code.isEmpty() || code.length < 6){
                        Snackbar.make(findViewById(R.id.codeInputConstraint), resources.getString(R.string.unfilled_code), Snackbar.LENGTH_LONG)
                            .show()
                    }
                    else {
                        URL("$ip/pair/$code").readText()
                        val codeInput = Intent(this, TimerActivity::class.java)
                        codeInput.putExtra("code",code)
                        startActivity(codeInput)
                    }

                } catch(e: Exception) {
                    Snackbar.make(findViewById(R.id.codeInputConstraint), resources.getString(R.string.pairing_failure), Snackbar.LENGTH_LONG)
                        .show()
                }

            }.start()

        }
        val underlines = arrayOf(
            findViewById<ImageView>(R.id.underline1),
            findViewById<ImageView>(R.id.underline2),
            findViewById<ImageView>(R.id.underline3),
            findViewById<ImageView>(R.id.underline4),
            findViewById<ImageView>(R.id.underline5),
            findViewById<ImageView>(R.id.underline6)
        )
        input.setOnEditorActionListener { p0, p1, p2 -> false }
        input.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                val regex = "[a-z0-9]".toRegex()
                val seq = s.filter {
                    regex.matches(it.toString())
                }
                if(seq.toString() != s.toString()) {
                    s.replace(0,s.length,seq)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                val length = s.length
                if(length == 0){
                    underlines[0].setImageResource(R.drawable.codeinput0)
                }
                else if(length == 6){
                    underlines[5].setImageResource(R.drawable.codeinput1)
                } else {
                    underlines[length-1].setImageResource(R.drawable.codeinput1)
                    underlines[length].setImageResource(R.drawable.codeinput0)
                }
            }
        })

    }
}