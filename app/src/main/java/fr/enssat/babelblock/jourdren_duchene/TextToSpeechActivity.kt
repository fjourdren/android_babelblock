package fr.enssat.babelblock.jourdren_duchene

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import fr.enssat.babelblock.jourdren_duchene.services.tts.TextToSpeechService
import kotlinx.android.synthetic.main.activity_text_to_speech.*
import kotlinx.android.synthetic.main.activity_text_to_speech.edit_query
import java.util.*


// inherit BaseActivity to manage menuInflater
class TextToSpeechActivity : BaseActivity(), AdapterView.OnItemSelectedListener {

    lateinit var serviceTTS: TextToSpeechService

    private var localesLanguagesUI = mutableListOf<String>()
    private var localesLanguagesObjects = mutableListOf<Locale>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set activity's layout
        setContentView(R.layout.activity_text_to_speech)

        // init text to speech service
        // use a callback to be sure that the speaker is initialized and that we can now use speaker's isLanguageAvailable func
        this.serviceTTS = TextToSpeechService(this.applicationContext, Locale.getDefault()) {
            /* === SPINNERS INIT === */
            // create language_spinner
            Locale.getAvailableLocales().forEach {
                // this.serviceTTS.speaker.availableLanguages can be used for android > 5.0
                if (it.country == "" && this.serviceTTS.speaker.isLanguageAvailable(it) >= 0) { // remove duplicated languages when this one is used in different countries (it just take the most basic one) && check that the speaker support this language
                    localesLanguagesObjects.add(it) // add to localesLanguagesObjects list
                }
            }

            // sort locales by displayLanguage
            localesLanguagesObjects = localesLanguagesObjects.sortedBy { it.displayLanguage } as MutableList<Locale>

            // foreach to make localesLanguagesUI with sorted localesLanguagesObjects (need to be done in two foreach because we can't order displayLanguage without doing that)
            localesLanguagesObjects.forEach {
                localesLanguagesUI.add(it.displayLanguage) // display language in the user's default locale
            }

            // make adapter
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, localesLanguagesUI)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // populate language_spinner
            language_spinner.adapter = adapter

            // set default language_spinner value
            language_spinner.setSelection(adapter.getPosition(Locale.getDefault().displayLanguage))
        }


        // language spinner listener
        language_spinner.onItemSelectedListener = this;


        // voice synthesizer button listener
        play_button.setOnClickListener {
            val text: String = edit_query.text.toString()
            this.serviceTTS.input = text // send text to the service
            this.serviceTTS.run()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        when(parent.id) {
            R.id.language_spinner -> {
                // get language
                val localeSelected: Locale = localesLanguagesObjects[pos]

                // set from language
                this.serviceTTS.locale = localeSelected
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onDestroy() {
        this.serviceTTS.close()
        super.onDestroy()
    }
}