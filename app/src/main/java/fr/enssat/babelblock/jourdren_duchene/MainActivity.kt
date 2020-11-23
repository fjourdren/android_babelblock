package fr.enssat.babelblock.jourdren_duchene

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

// inherit BaseActivity to manage menuInflater
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set activity's layout
        setContentView(R.layout.activity_main)

        // manage user's click on buttons to access other activities
        // Text to speech button listener
        main_button_tts.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_DOWN) {
                val intent = Intent(this, TextToSpeechActivity::class.java)
                startActivity(intent)
            }
            false
        }

        // Translate button listener
        main_button_translate.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_DOWN) {
                val intent = Intent(this, TranslatorActivity::class.java)
                startActivity(intent)
            }
            false
        }

        // Speech to text button listener
        main_button_stt.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_DOWN) {
                val intent = Intent(this, SpeechToTextActivity::class.java)
                startActivity(intent)
            }
            false
        }

        // Block demo button listener
        main_button_block.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_DOWN) {
                val intent = Intent(this, BlockActivity::class.java)
                startActivity(intent)
            }
            false
        }

        // Translation pipelining button listener
        main_button_pipeline.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_DOWN) {
                Toast.makeText(this, "TODO", Toast.LENGTH_LONG).show()
            }
            false
        }
    }
}