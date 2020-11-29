package fr.enssat.babelblock.jourdren_duchene

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import fr.enssat.babelblock.jourdren_duchene.services.translation.Language
import fr.enssat.babelblock.jourdren_duchene.services.translation.TranslatorService
import kotlinx.android.synthetic.main.activity_translator.*
import java.util.*

// inherit BaseActivity to manage menuInflater
class TranslatorActivity : BaseActivity(), AdapterView.OnItemSelectedListener {

    lateinit var translatorService: TranslatorService

    private var localesLanguagesUI = mutableListOf<String>()
    private var localesLanguagesObjects = mutableListOf<Locale>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set activity's layout
        setContentView(R.layout.activity_translator)


        /* === SPINNERS INIT === */
        // create adapter's array & sort locales by displayLanguage
        localesLanguagesObjects = Language.generateMLKitAvailableLocales().sortedBy { it.displayLanguage } as MutableList<Locale>

        // foreach to make localesLanguagesUI with sorted localesLanguagesObjects (need to be done in two foreach because we can't order displayLanguage without doing that)
        localesLanguagesObjects.forEach {
            localesLanguagesUI.add(it.displayLanguage) // display language in the user's default locale
        }



        // create to_language_spinner and from_language_spinner adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, localesLanguagesUI)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)





        // populate from_language_spinner
        val from_language_spinner = findViewById<Spinner>(R.id.from_language_spinner)
        from_language_spinner.adapter = adapter

        // set default from_language_spinner
        from_language_spinner.setSelection(adapter.getPosition(Locale.FRENCH.displayLanguage))



        // populate to_language_spinner
        val to_language_spinner = findViewById<Spinner>(R.id.to_language_spinner)
        to_language_spinner.adapter = adapter

        // set default to_language_spinner
        to_language_spinner.setSelection(adapter.getPosition(Locale.ENGLISH.displayLanguage))



        /* === SERVICES INIT === */
        // create translation service
        this.translatorService = TranslatorService(this, Locale.FRENCH,  Locale.ENGLISH)


        /* === BUTTON LISTENERS === */
        translate_button.setOnClickListener {
            this.translatorService.run(edit_query.text.toString()) { enText ->
                translated_text.text = enText
            }
        }

        // language spinner listeners
        from_language_spinner.setOnItemSelectedListener(this);
        to_language_spinner.setOnItemSelectedListener(this);
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        when(parent.id) {
            R.id.from_language_spinner -> {
                // set from language
                this.translatorService.from_language = localesLanguagesObjects[pos]

                // manage UI & download model
                this.downloadTranslationModel()
            }

            R.id.to_language_spinner -> {
                // set target language
                this.translatorService.to_language = localesLanguagesObjects[pos]

                // manage UI & download model
                this.downloadTranslationModel()
            }
        }
    }

    private fun downloadTranslationModel() {
        translate_button.isEnabled = false;
        translate_button.isEnabled = false;

        info_message.text = "Downloading translation model... Please wait."
        translated_text.text = "" // force reseting output value when we change value

        this.translatorService.downloadModelIfNeeded({
            translate_button.isEnabled = true;
            translate_button.isEnabled = true;
            info_message.text = "State: Ready."
        }, {
            translate_button.isEnabled = false;
            translate_button.isEnabled = false;
            info_message.text = "Error: Model download failed, retrying..."
        }, {
            info_message.text = "Error: App can't download the translation model, please check your wifi connection or used languages..."
        })
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onDestroy() {
        this.translatorService.close()
        super.onDestroy()
    }
}