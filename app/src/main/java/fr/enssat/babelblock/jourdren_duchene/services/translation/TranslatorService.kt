/*package fr.enssat.babelblock.jourdren_duchene.services.translation

import android.content.Context
import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import fr.enssat.babelblock.jourdren_duchene.services.Service
import java.util.Locale

// we use MLKit way to say language (which are store in TranslateLanguage enum as strings)
class TranslatorService(context: Context, from: String = TranslateLanguage.FRENCH, to: String = TranslateLanguage.ENGLISH, downloadModelSuccess: () -> Unit = {}, downloadModelFailed: () -> Unit = {}): Service(context) {

    // MLKit translator instance
    lateinit var translator: Translator

    var from_language: String = from
        set(new_from) { // we make the service able to modify local to change languages by using a setter
            this.from_language = new_from
            this.makeTranslator()
        }
    private var to_language: String = to
        set(new_to) { // we make the service able to modify local to change languages by using a setter
            this.to_language = new_to
            this.makeTranslator()
        }


    // at init, make the model and download it
    init {
        this.makeTranslator(downloadModelSuccess, downloadModelFailed)
    }


    fun run(text: String, callback: (String) -> Unit) {
        // execute translation
        this.translator.translate(text).addOnSuccessListener(callback).addOnFailureListener { e -> Log.e("TranslatorService", "Translation failed", e) }
    }

    override fun close() {
        this.translator.close()
    }


    // build translator instance and start downloading the needed model
    private fun makeTranslator(downloadModelSuccess: () -> Unit = {}, downloadModelFailed: () -> Unit = {}) {
        val translatorOptions = TranslatorOptions.Builder().setSourceLanguage(this.from_language).setTargetLanguage(this.to_language).build() // configure languages
        this.translator = Translation.getClient(translatorOptions)
        this.downloadModelIfNeeded(downloadModelSuccess, downloadModelFailed)
    }

    // download the model if needed
    private fun downloadModelIfNeeded(downloadModelSuccess: () -> Unit = {}, downloadModelFailed: () -> Unit = {}) {
        // apply the download condition => download if the model isn't on the device and that device is connected in wifi
        this.translator.downloadModelIfNeeded(DownloadConditions.Builder().requireWifi().build())
                .addOnSuccessListener {
                    Log.d("TranslatorService", "Translation model download completed")
                    downloadModelSuccess // callback success execution
                }.addOnFailureListener {
                    e -> Log.e("TranslatorService", "Translation model download failed ", e)
                    downloadModelFailed // callback failure execution
                }
    }
}*/



package fr.enssat.babelblock.jourdren_duchene.services.translation

import android.content.Context
import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import fr.enssat.babelblock.jourdren_duchene.services.Service

class TranslatorService(context: Context, from: String, to: String, downloadModelSuccess: () -> Unit = {}, downloadModelFailed: () -> Unit = {}): Service(context) {

    private val downloadConditions = DownloadConditions.Builder().requireWifi().build() // make a condition object which describe condition needed to start downloading a model
    private val translatorOptions = TranslatorOptions.Builder().setSourceLanguage(from).setTargetLanguage(to).build() // configure languages
    private val translator = Translation.getClient(translatorOptions)

    init {
        // apply the download condition => download if the model isn't on the device and that device is connected in wifi
        translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener {
                    Log.d("TranslatorService", "Translation model download completed")
                    downloadModelSuccess // callback success execution
                }.addOnFailureListener {
                    e -> Log.e("TranslatorService", "Translation model download failed ", e)
                    downloadModelFailed // callback failure execution
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