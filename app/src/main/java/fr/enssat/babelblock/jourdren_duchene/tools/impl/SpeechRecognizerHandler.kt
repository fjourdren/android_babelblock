package fr.enssat.babelblock.jourdren_duchene.tools.impl

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import fr.enssat.babelblock.jourdren_duchene.tools.ServiceDemo
import fr.enssat.babelblock.jourdren_duchene.tools.SpeechToTextTool
import java.lang.IllegalStateException
import java.util.*

class SpeechRecognizerHandler: ServiceDemo {
    var locale: Locale

    private var speechRecognizer: SpeechRecognizer
    private var listener: SpeechToTextTool.Listener
    private var intent: Intent

    constructor(context: Context, locale: Locale, listener: SpeechToTextTool.Listener): super(context) {
        this.locale = locale
        this.listener = listener

        if(SpeechRecognizer.isRecognitionAvailable(context).not()) {
            Log.e("Reco", "Sorry but Speech recognizer is not available on this device")
            throw IllegalStateException()
        }

        this.speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object: RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) { Log.d("Reco", "ready $params") }
                override fun onRmsChanged(rmsdB: Float) { }
                override fun onBufferReceived(buffer: ByteArray?) { }
                override fun onEndOfSpeech() { }
                override fun onError(error: Int) { Log.d("Reco", "Error : $error") }

                override fun onBeginningOfSpeech() {
                    Log.d("Reco", "Listening")
                    listener?.onResult("Listening...", false)
                }

                private fun Bundle.getResult(): String? = this.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)
                override fun onPartialResults(partialResults: Bundle?) {
                    Log.d("Reco", "partial : $partialResults")
                    partialResults?.getResult()?.apply {
                        Log.d("Reco", "partial : $this")
                        listener?.onResult(this, false)
                    }
                }

                override fun onResults(results: Bundle?) {
                    Log.d("Reco", "result : $results")
                    results?.getResult()?.apply {
                        Log.d("Reco", "result : $this")
                        listener?.onResult(this, true)
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) { }
            })
        }

        this.intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale)
        }
    }

    override fun run() {
        speechRecognizer.startListening(this.intent)
    }

    override fun stop() {
        speechRecognizer.stopListening()
        speechRecognizer.cancel()
    }

    override fun close() {
        speechRecognizer.destroy()
    }
}