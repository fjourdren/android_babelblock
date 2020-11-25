package fr.enssat.babelblock.jourdren_duchene.services.translation

import android.content.Context
import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import fr.enssat.babelblock.jourdren_duchene.services.Service

class TranslatorService: Service {

    var from_language: String = TranslateLanguage.FRENCH
        set(new_lang) {  // force build the translator with new language by using a setter
            field = new_lang
            this.buildObject()
        }
    var to_language: String = TranslateLanguage.ENGLISH
        set(new_lang) {  // force build the translator with new language by using a setter
            field = new_lang
            this.buildObject()
        }

    private lateinit var translatorOptions: TranslatorOptions
    private lateinit var translator: Translator

    constructor(context: Context, from_language: String, to_language: String): super(context) {
        this.from_language = from_language
        this.to_language = to_language

        downloadModelIfNeeded()
    }

    fun buildObject() {
        this.translatorOptions = TranslatorOptions.Builder().setSourceLanguage(this.from_language).setTargetLanguage(this.to_language).build() // configure languages
        this.translator = Translation.getClient(this.translatorOptions)
    }

    fun downloadModelIfNeeded(downloadModelSuccess: () -> Unit = {}, downloadModelFailed: () -> Unit = {}) {
        // make a condition object which describe condition needed to start downloading a model
        val downloadConditions = DownloadConditions.Builder().requireWifi().build()

        // apply the download condition => download if the model isn't on the device and that device is connected in wifi
        this.translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener {
                    Log.d("TranslatorService", "Translation model download completed")
                    downloadModelSuccess.invoke() // callback success execution
                }.addOnFailureListener {
                    e -> Log.e("TranslatorService", "Translation model download failed ", e)
                    downloadModelFailed.invoke() // callback failure execution
                }
    }

    fun run(text: String, callback: (String) -> Unit) {
        // execute translation
        this.translator.translate(text).addOnSuccessListener(callback).addOnFailureListener { e -> Log.e("TranslatorService", "Translation failed", e) }
    }

    override fun close() {
        this.translator.close()
    }
}