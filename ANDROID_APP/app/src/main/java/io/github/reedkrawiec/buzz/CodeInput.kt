package io.github.reedkrawiec.buzz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText

class CodeInput : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_input)
        val button = findViewById<Button>(R.id.enterCodeButton);
        val input = findViewById<EditText>(R.id.codeInput);
        button.setOnClickListener{
            val codeInput = Intent(this, MainActivity::class.java)
            Log.d("TAG",input.text.toString());
            codeInput.putExtra("code",input.text.toString())
            startActivity(codeInput)
        }
    }
}