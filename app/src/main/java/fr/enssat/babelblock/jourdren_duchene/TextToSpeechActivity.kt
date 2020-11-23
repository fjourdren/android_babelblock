package fr.enssat.babelblock.jourdren_duchene

import android.os.Bundle
import fr.enssat.babelblock.jourdren_duchene.services.Service
import fr.enssat.babelblock.jourdren_duchene.services.tts.TextToSpeechService

import kotlinx.android.synthetic.main.activity_text_to_speech.*
import java.util.*


// inherit BaseActivity to manage menuInflater
class TextToSpeechActivity : BaseActivity() {

    lateinit var service: TextToSpeechService;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set activity's layout
        setContentView(R.layout.activity_text_to_speech)

        // init text to speech service
        this.service = TextToSpeechService(this.applicationContext, Locale.getDefault())

        // voice synthesizer button listener
        play_button.setOnClickListener {
            val text: String = edit_query.text.toString()
            this.service.input = text // send text to the service
            this.service.run()
        }
    }

    override fun onDestroy() {
        this.service.close()
        super.onDestroy()
    }
}