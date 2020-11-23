package fr.enssat.babelblock.jourdren_duchene

import android.os.Bundle
import fr.enssat.babelblock.jourdren_duchene.services.translation.TranslatorService
import kotlinx.android.synthetic.main.activity_translator.*
import java.util.*

// inherit BaseActivity to manage menuInflater
class TranslatorActivity : BaseActivity() {

    lateinit var translator: TranslatorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set activity's layout
        setContentView(R.layout.activity_translator)

        // create translation service
        this.translator = TranslatorService(this, Locale.FRENCH, Locale.ENGLISH, {
            translate_button.isEnabled = true;
            translate_button.isEnabled = true;
        }, {
            translate_button.isEnabled = false;
            translate_button.isEnabled = false;
            translated_text.text = "App can't download the translation model, please check your wifi connection..."
        })

        // translate button listener
        translate_button.setOnClickListener {
            this.translator.run(edit_query.text.toString()) { enText ->
                translated_text.text = enText
            }
        }
    }

    override fun onDestroy() {
        this.translator.close()
        super.onDestroy()
    }
}