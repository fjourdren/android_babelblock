package fr.enssat.babelblock.jourdren_duchene.tools.impl

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import fr.enssat.babelblock.jourdren_duchene.TextToSpeechActivity
import fr.enssat.babelblock.jourdren_duchene.tools.ServiceDemo
import java.util.*

class TextToSpeechHandler: ServiceDemo {
    var locale: Locale
    var speaker : TextToSpeech

    constructor(context: Context, locale: Locale): super(context) {
        this.locale = locale

        speaker = TextToSpeech(context, object : TextToSpeech.OnInitListener {
            override fun onInit(status: Int) {
                Log.d("Speak", "status: $status")
            }
        })
    }



    override fun run() {
        speaker.language = locale
        speaker.speak(super.input, TextToSpeech.QUEUE_FLUSH, null)
    }

    override fun stop() {
        speaker.stop()
    }

    override fun close() {
        speaker.shutdown()
    }
}