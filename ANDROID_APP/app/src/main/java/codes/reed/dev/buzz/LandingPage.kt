package codes.reed.dev.buzz


import android.content.Intent
import android.os.Bundle
import android.widget.Button

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