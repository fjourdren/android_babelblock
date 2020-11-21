package fr.enssat.babelblock.jourdren_duchene

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.enssat.babelblock.jourdren_duchene.tools.ServiceDemo
import fr.enssat.babelblock.jourdren_duchene.tools.impl.TextToSpeechHandler

import kotlinx.android.synthetic.main.activity_text_to_speech.*
import java.util.*


class TextToSpeechActivity : BaseActivity() {

    lateinit var service: ServiceDemo;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_text_to_speech)

        //cf build.gradle kotlin-android-extensions
        //cf import kotlinx.android.synthetic.main.activity_text_to_speech.*
        //say goodbye to findviewbyid, recovering and binding view from layout

        this.service = TextToSpeechHandler(this.applicationContext, Locale.getDefault())

        play_button.setOnClickListener {
            val text: String = edit_query.text.toString()
            (this.service as TextToSpeechHandler).input = text
            this.service.run()
        }
    }

    override fun onDestroy() {
        this.service.close()
        super.onDestroy()
    }
}