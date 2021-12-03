package io.github.reedkrawiec.buzz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

class CodeInput : BaseActivity() {
    private var isListening: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_input)
        registerSettingsButton("CodeInput","")
        val button = findViewById<Button>(R.id.enterCodeButton)
        val input = findViewById<EditText>(R.id.codeInput)
        val codeHelp = findViewById<TextView>(R.id.codeHelp)
        codeHelp.setOnClickListener{
            dialog("help:",resources.getString(R.string.code_help))
        }
        button.setOnClickListener{
            val codeInput = Intent(this, TimerActivity::class.java)
            codeInput.putExtra("code",input.text.toString())
            startActivity(codeInput)
        }
        val underlines = arrayOf(
            findViewById<ImageView>(R.id.underline1),
            findViewById<ImageView>(R.id.underline2),
            findViewById<ImageView>(R.id.underline3),
            findViewById<ImageView>(R.id.underline4),
            findViewById<ImageView>(R.id.underline5),
            findViewById<ImageView>(R.id.underline6)
        )
        input.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

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