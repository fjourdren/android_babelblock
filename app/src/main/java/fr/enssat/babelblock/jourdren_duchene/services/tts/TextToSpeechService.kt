package fr.enssat.babelblock.jourdren_duchene.services.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.LANG_AVAILABLE
import android.util.Log
import fr.enssat.babelblock.jourdren_duchene.services.Service
import java.util.*

class TextToSpeechService: Service {
    var speaker: TextToSpeech

    var locale: Locale
        set(new_locale) {  // force build the translator with new language by using a setter
            // set speaker's language if speaker has already been initialised
            if(this.speaker != null) {
                // if the language isn't available for speaker, we set default user's locale
                field = if(this.speaker.isLanguageAvailable(new_locale) < 0)
                    Locale.getDefault()
                else
                    new_locale

                // update speaker language
                this.speaker.language = locale

                Log.d("TextToSpeechService", "Speaker uses: ${field.language}, User's selection: ${new_locale.language}")
            }
        }

    constructor(context: Context, locale: Locale, callback: () -> Unit = {}): super(context) {
        // init locale
        this.locale = locale

        // init android's text to speech service
        this.speaker = TextToSpeech(context) { status ->
            Log.d("TextToSpeechService", "Status: $status")
            callback.invoke()
        }
    }

    override fun run() {
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