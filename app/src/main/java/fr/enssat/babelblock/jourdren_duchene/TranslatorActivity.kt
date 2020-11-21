package fr.enssat.babelblock.jourdren_duchene

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import fr.enssat.babelblock.jourdren_duchene.tools.impl.TranslatorHandler
import kotlinx.android.synthetic.main.activity_translator.*
import java.util.*

class TranslatorActivity : BaseActivity() {

    lateinit var translator: TranslatorHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translator)

        this.translator = TranslatorHandler(this, Locale.FRENCH, Locale.ENGLISH, {
            translate_button.isEnabled = true;
            translate_button.isEnabled = true;
        }, {
            translate_button.isEnabled = false;
            translate_button.isEnabled = false;
            translated_text.text = "App can't download the translation model, please check your wifi connection..."
        })

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