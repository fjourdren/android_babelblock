package fr.enssat.babelblock.jourdren_duchene

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import fr.enssat.babelblock.jourdren_duchene.services.stt.Listener
import fr.enssat.babelblock.jourdren_duchene.services.stt.SpeechToTextService
import kotlinx.android.synthetic.main.activity_stt.*
import java.util.*

// inherit BaseActivity to manage menuInflater
class SpeechToTextActivity : BaseActivity(), AdapterView.OnItemSelectedListener {
    private val RecordAudioRequestCode = 1

    lateinit var serviceSTT: SpeechToTextService

    private var localesLanguagesUI = mutableListOf<String>()
    private var localesLanguagesObjects = mutableListOf<Locale>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set activity's layout
        setContentView(R.layout.activity_stt)


        // check that app has permission to listen
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // request permission if needed
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RecordAudioRequestCode)
        }

        // start android's STT service and give him a callback (via a Listener)
        this.serviceSTT = SpeechToTextService(this, Locale.getDefault(), object: Listener {
            override fun onResult(text: String, final: Boolean) {
                //if(final) {
                    Log.d("SpeechToTextActivity", "Final: $text")
                    recognized_text.text = text
                //}
            }
        })



        /* === SPINNERS INIT === */
        // create adapter's array
        Locale.getAvailableLocales().forEach {
            if (it.country == "") { // remove duplicated languages when this one is used in different countries (it just take the most basic one)
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



        // record_button listener
        record_button.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_DOWN) {
                Log.d("SpeechToTextActivity UI", "STT button pressed")
                v.performClick()
                this.serviceSTT.run()
            } else if(event.action == MotionEvent.ACTION_UP) {
                Log.d("SpeechToTextActivity UI", "STT button released")
                this.serviceSTT.stop()
            }
            false
        }


        // language spinner listener
        language_spinner.onItemSelectedListener = this;
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        when(parent.id) {
            R.id.language_spinner -> {
                // get language
                val localeSelected: Locale = localesLanguagesObjects[pos]

                // set from language
                this.serviceSTT.locale = localeSelected
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onDestroy() {
        this.serviceSTT.close()
        super.onDestroy()
    }

    // Listener when permission to listen is give to the app
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == RecordAudioRequestCode && grantResults.size > 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

}