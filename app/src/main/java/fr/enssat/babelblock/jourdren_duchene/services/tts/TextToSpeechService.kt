package fr.enssat.babelblock.jourdren_duchene.services.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import fr.enssat.babelblock.jourdren_duchene.services.Service
import java.util.*

class TextToSpeechService: Service {
    var locale: Locale
    var speaker: TextToSpeech

    constructor(context: Context, locale: Locale): super(context) {
        this.locale = locale

        // init android's text to speech service
        speaker = TextToSpeech(context) { status -> Log.d("TextToSpeechService", "Status: $status") }
    }

    override fun run() {
        // set speaker's language
        speaker.language = locale

        // run the synthesizer
        speaker.speak(super.input, TextToSpeech.QUEUE_FLUSH, null)
    }

    override fun stop() {
        speaker.stop()
    }

    override fun close() {
        speaker.shutdown()
    }
}