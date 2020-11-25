/*package fr.enssat.babelblock.jourdren_duchene

import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.mlkit.nl.translate.TranslateLanguage
import fr.enssat.babelblock.jourdren_duchene.services.translation.TranslatorService
import kotlinx.android.synthetic.main.activity_translator.*


// inherit BaseActivity to manage menuInflater
class TranslatorActivity: BaseActivity(), AdapterView.OnItemSelectedListener {

    lateinit var translator: TranslatorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set activity's layout
        setContentView(R.layout.activity_translator)

        // spinner
        val to_language_spinner = findViewById<Spinner>(R.id.to_language_spinner)



        val adapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        to_language_spinner.adapter = adapter


        /*var languages = TranslateLanguage.getAllLanguages().toTypedArray()
        languages = languages.map { TranslateLanguage.fromLanguageTag(it) }.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        spinner.adapter = adapter*/


        // create translation service
        /*translate_button.isEnabled = false;
        translate_button.isEnabled = false;*/
        this.translator = TranslatorService(this, TranslateLanguage.FRENCH, TranslateLanguage.RUSSIAN, {
            translate_button.isEnabled = true;
            translate_button.isEnabled = true;
        }) {
            translate_button.isEnabled = false;
            translate_button.isEnabled = false;
            translated_text.text = "App can't download the translation model, please check your wifi connection..."
        }

        // translate button listener
        translate_button.setOnClickListener {
            this.translator.run(edit_query.text.toString()) { enText ->
                translated_text.text = enText
            }
        }

        // from language spinner listeners
        to_language_spinner.setOnItemSelectedListener(this);
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        var lang_txt = parent.getItemAtPosition(pos).toString();
        Toast.makeText(parent.context, lang_txt, Toast.LENGTH_LONG).show()
        this.translator.to_language = "en"
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        this.translator.close()
        super.onDestroy()
    }
}*/


package fr.enssat.babelblock.jourdren_duchene

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import fr.enssat.babelblock.jourdren_duchene.services.translation.TranslatorService
import fr.enssat.babelblock.jourdren_duchene.services.translation.languages
import kotlinx.android.synthetic.main.activity_translator.*

// inherit BaseActivity to manage menuInflater
class TranslatorActivity : BaseActivity(), AdapterView.OnItemSelectedListener {

    lateinit var translatorService: TranslatorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set activity's layout
        setContentView(R.layout.activity_translator)


        /* === SPINNERS INIT === */
        // create to_language_spinner and from_language_spinner adapter
        val ui_languages = languages.keys.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ui_languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)



        // generate default language spinners values
        val default_from_language_string = "french"
        val default_to_language_string   = "english"



        // populate from_language_spinner
        val from_language_spinner = findViewById<Spinner>(R.id.from_language_spinner)
        from_language_spinner.adapter = adapter

        // set default from_language_spinner
        val default_from_language_spinner: Int = adapter.getPosition(default_from_language_string)
        from_language_spinner.setSelection(default_from_language_spinner)



        // populate to_language_spinner
        val to_language_spinner = findViewById<Spinner>(R.id.to_language_spinner)
        to_language_spinner.adapter = adapter

        // set default to_language_spinner
        val default_to_language_spinner: Int = adapter.getPosition(default_to_language_string)
        to_language_spinner.setSelection(default_to_language_spinner)



        /* === SERVICES INIT === */
        // create translation service
        this.translatorService = TranslatorService(this, languages.get(default_from_language_string).toString(),  languages.get(default_to_language_string).toString())


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
                var from_language = parent.getItemAtPosition(pos).toString();
                this.translatorService.from_language = languages.get(from_language).toString()

                translate_button.isEnabled = false;
                translate_button.isEnabled = false;
                info_message.text = "State: Downloading translation model... Please wait."
                this.translatorService.downloadModelIfNeeded({
                    translate_button.isEnabled = true;
                    translate_button.isEnabled = true;
                    info_message.text = "State: Ready."
                }, {
                    translate_button.isEnabled = false;
                    translate_button.isEnabled = false;
                    info_message.text = "Error: App can't download the translation model, please check your wifi connection or used languages..."
                })
            }

            R.id.to_language_spinner -> {
                var to_language = parent.getItemAtPosition(pos).toString();
                this.translatorService.to_language = languages.get(to_language).toString()

                translate_button.isEnabled = false;
                translate_button.isEnabled = false;
                info_message.text = "Downloading translation model... Please wait."
                this.translatorService.downloadModelIfNeeded({
                    translate_button.isEnabled = true;
                    translate_button.isEnabled = true;
                    info_message.text = "State: Ready."
                }, {
                    translate_button.isEnabled = false;
                    translate_button.isEnabled = false;
                    info_message.text = "Error: App can't download the translation model, please check your wifi connection or used languages..."
                })
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onDestroy() {
        this.translatorService.close()
        super.onDestroy()
    }
}