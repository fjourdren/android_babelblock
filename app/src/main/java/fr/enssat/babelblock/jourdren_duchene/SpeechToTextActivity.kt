package fr.enssat.babelblock.jourdren_duchene

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import fr.enssat.babelblock.jourdren_duchene.services.stt.Listener
import fr.enssat.babelblock.jourdren_duchene.services.stt.SpeechToTextService
import kotlinx.android.synthetic.main.activity_speech_to_text.*
import java.util.*

// inherit BaseActivity to manage menuInflater
class SpeechToTextActivity : BaseActivity() {
    private val RecordAudioRequestCode = 1

    lateinit var speechToText: SpeechToTextService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set activity's layout
        setContentView(R.layout.activity_speech_to_text)

        // check that app has permission to listen
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // request permission if needed
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RecordAudioRequestCode)
        }

        // start android's STT service and give him a callback (via a Listener)
        speechToText = SpeechToTextService(this, Locale.getDefault(), object: Listener {
            override fun onResult(text: String, final: Boolean) {
                //if(final) {
                    Log.d("SpeechToTextActivity", "Final: $text")
                    recognized_text.text = text
                //}
            }
        })

        // record_button listener
        record_button.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_DOWN) {
                Log.d("SpeechToTextActivity UI", "STT button pressed")
                v.performClick()
                speechToText.run()
            } else if(event.action == MotionEvent.ACTION_UP) {
                Log.d("SpeechToTextActivity UI", "STT button releases")
                speechToText.stop()
            }
            false
        }
    }

    override fun onDestroy() {
        speechToText.close()
        super.onDestroy()
    }

    // Listener when permission to listen is give to the app
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == RecordAudioRequestCode && grantResults.size > 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            }
        }
    }


}