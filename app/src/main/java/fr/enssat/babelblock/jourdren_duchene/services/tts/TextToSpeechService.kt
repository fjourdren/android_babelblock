package fr.enssat.babelblock.jourdren_duchene.services.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import fr.enssat.babelblock.jourdren_duchene.services.Service
import java.util.*

class TextToSpeechService: Service {
    var speaker: TextToSpeech

    var locale: Locale

    var serviceReady = false

    var localesLanguagesUI = mutableListOf<String>()
    var localesLanguagesObjects = mutableListOf<Locale>()

    constructor(context: Context, locale: Locale, callback: () -> Unit = {}): super(context) {
        // init locale
        this.locale = locale

        // init android's text to speech service
        Log.d("INIT", "INIT")
        this.speaker = TextToSpeech(context) { status ->
            Log.d("TextToSpeechService", "Status: $status")
            this.serviceReady = true
            callback.invoke()
        }
    }

    private fun buildTTS() {
        // set speaker's language if speaker has already been initialised
        if(this.speaker != null) {
            // if the language isn't available for speaker, we set default user's locale
            if(this.speaker.isLanguageAvailable(this.locale) < 0)
                this.locale = Locale.getDefault()

            // update speaker language
            this.speaker.language = locale
        }
    }

    fun run() {
        // update speaker's language
        buildTTS()

        // run the synthesizer
        this.speaker.speak(super.input, TextToSpeech.QUEUE_FLUSH, null)
    }

    override fun stop() {
        this.speaker.stop()
    }

    override fun close() {
        this.speaker.shutdown()
    }
}