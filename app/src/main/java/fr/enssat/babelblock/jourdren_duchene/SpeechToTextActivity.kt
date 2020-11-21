package fr.enssat.babelblock.jourdren_duchene

import fr.enssat.babelblock.jourdren_duchene.tools.SpeechToTextTool
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import fr.enssat.babelblock.jourdren_duchene.tools.impl.SpeechRecognizerHandler
import kotlinx.android.synthetic.main.activity_speech_to_text.*
import java.util.*

class SpeechToTextActivity : BaseActivity() {
    private val RecordAudioRequestCode = 1

    lateinit var speechToText: SpeechRecognizerHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speech_to_text)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission()
        }

        speechToText = SpeechRecognizerHandler(this, Locale.getDefault(), object : SpeechToTextTool.Listener {
            override fun onResult(text: String, isFinal: Boolean) {
                //if (isFinal) {
                    Log.d("Reco", "Final: $text")
                    recognized_text.text = text
                //}
            }
        })

        record_button.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                Log.d("Reco UI", "Button pressed")
                v.performClick()
                speechToText.run()
            } else if (event.action == MotionEvent.ACTION_UP) {
                Log.d("Reco UI", "Button releases")
                speechToText.stop()
            }
            false
        }
    }

    override fun onDestroy() {
        speechToText.close()
        super.onDestroy()
    }

    private fun checkPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RecordAudioRequestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RecordAudioRequestCode && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }


}