package fr.enssat.babelblock.jourdren_duchene.services.translation

import android.content.Context
import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModel
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.*
import fr.enssat.babelblock.jourdren_duchene.services.Service
import kotlinx.android.synthetic.main.activity_translator.*
import java.util.*

class TranslatorService: Service {

    var from_language: Locale = Locale.FRENCH
        set(new_lang) {  // force build the translator with new language by using a setter
            try {
                this.deleteModel(this.from_language) // delete old from language model (adviced in the doc)
            } catch (e: Exception) {}

            field = new_lang
            this.buildTranslator()
        }
    var to_language: Locale = Locale.ENGLISH
        set(new_lang) {  // force build the translator with new language by using a setter
            try {
                this.deleteModel(this.to_language) // delete old to language model (adviced in the doc)
            } catch (e: Exception) {}
            field = new_lang
            this.buildTranslator()
        }

    private lateinit var translatorOptions: TranslatorOptions
    private lateinit var translator: Translator

    private val modelManager = RemoteModelManager.getInstance()

    constructor(context: Context, from_language: Locale, to_language: Locale): super(context) {
        this.from_language = from_language
        this.to_language = to_language

        // we download model
        downloadModelIfNeeded()
    }

    private fun buildTranslator() {
        this.translatorOptions = TranslatorOptions.Builder().setSourceLanguage(this.from_language.language).setTargetLanguage(this.to_language.language).build() // configure languages
        this.translator = Translation.getClient(this.translatorOptions)
    }

    fun downloadModelIfNeeded(downloadModelSuccess: () -> Unit = {}, downloadModelFailed: () -> Unit = {}, finalDownloadModelFailed: () -> Unit = {}) {
        // make a condition object which describe condition needed to start downloading a model
        val downloadConditions = DownloadConditions.Builder().requireWifi().build()

        // apply the download condition => download if the model isn't on the device and that device is connected in wifi
        this.translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener {
                    Log.d("Model Management", "Translation model download completed")
                    downloadModelSuccess.invoke() // callback success execution
                }.addOnFailureListener { e -> Log.e("Model Management", "Translation model download failed. Retrying... ", e)
                    downloadModelFailed.invoke() // callback failure execution

                    /***** Retry to download models but with lower level MLKit functions *****/
                    // delete both models by hand if an error occurs
                    this.deleteModel(this.from_language)
                    this.deleteModel(this.to_language)

                    // retry to re-download both models
                    val modelFrom =  TranslateRemoteModel.Builder(this.from_language.language).build()
                    val modelTo =  TranslateRemoteModel.Builder(this.to_language.language).build()

                    // downloading two models
                    modelManager.download(modelFrom, downloadConditions).addOnFailureListener {
                        Log.e("Model Management", "Can't download $modelFrom model")
                        finalDownloadModelFailed.invoke() // callback failure execution
                    }
                    modelManager.download(modelTo, downloadConditions).addOnFailureListener {
                        Log.e("Model Management", "Can't download $modelTo model")
                        finalDownloadModelFailed.invoke() // callback failure execution
                    }
                }
    }

    override fun run(text: String, callback: (String) -> Unit) {
        // execute translation
        this.translator.translate(text).addOnSuccessListener { enText ->
            super.output = enText
            callback.invoke(enText)
        }.addOnFailureListener { e -> Log.e("TranslatorService", "Translation failed", e) }
    }

    private fun deleteModel(languageToDelete: Locale) {
        // get model to delete
        val model =  TranslateRemoteModel.Builder(languageToDelete.language).build()
        val modelRemote = model as RemoteModel

        // if the model exists, to be sure that we try to delete an existing model (absolutely needed, otherwise our dynamic model deletion makes app crash)
        this.modelManager.isModelDownloaded(modelRemote).addOnSuccessListener { modelExists ->
            if(modelExists && model.language != Locale.getDefault().language && model.language != Locale.ENGLISH.language) { // check also that the model in deletion isn't the main phone language or english
                Log.d("Model Management", "Deleting ${model.language} model")

                // delete any previous downloaded models
                this.modelManager.deleteDownloadedModel(model)
            }
        }.addOnFailureListener {}
    }

    override fun close() {
        this.translator.close()
    }
}