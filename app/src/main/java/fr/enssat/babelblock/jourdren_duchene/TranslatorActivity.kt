package fr.enssat.babelblock.jourdren_duchene

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.enssat.babelblock.jourdren_duchene.tools.BlockService
import fr.enssat.babelblock.jourdren_duchene.tools.TranslationTool
import kotlinx.android.synthetic.main.activity_translator.*
import java.util.*

class TranslatorActivity : AppCompatActivity() {

    lateinit var translator: TranslationTool

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translator)

        val service = BlockService(this)
        translator = service.translator(Locale.FRENCH, Locale.ENGLISH)

        translate_button.setOnClickListener {
            translator.translate(edit_query.text.toString()) { enText ->
                translated_text.text = enText
            }
        }
    }

    override fun onDestroy() {
        translator.close()
        super.onDestroy()
    }
}