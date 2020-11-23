package fr.enssat.babelblock.jourdren_duchene.services.translation

import android.content.Context
import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import fr.enssat.babelblock.jourdren_duchene.services.Service
import java.util.Locale

class TranslatorService(context: Context, from: Locale, to: Locale, downloadModelSuccess: () -> Unit = {}, downloadModelFailed: () -> Unit = {}): Service(context) {

    private val downloadConditions = DownloadConditions.Builder().requireWifi().build() // make a condition object which describe condition needed to start downloading a model
    private val translatorOptions = TranslatorOptions.Builder().setSourceLanguage(from.language).setTargetLanguage(to.language).build() // configure languages
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