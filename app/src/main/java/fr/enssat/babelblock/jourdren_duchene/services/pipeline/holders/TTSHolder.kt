package fr.enssat.babelblock.jourdren_duchene.services.pipeline.holders

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.enssat.babelblock.jourdren_duchene.R
import fr.enssat.babelblock.jourdren_duchene.services.pipeline.Tool
import fr.enssat.babelblock.jourdren_duchene.services.pipeline.ToolChain
import fr.enssat.babelblock.jourdren_duchene.services.tts.TextToSpeechService
import kotlinx.android.synthetic.main.block_tts.view.*
import kotlinx.android.synthetic.main.block_tts.view.tool_title
import kotlinx.android.synthetic.main.block_tts.view.input_value
import kotlinx.android.synthetic.main.block_tts.view.output_value
import java.util.*

// Text to speech tool holder
class TTSHolder(val view: View): RecyclerView.ViewHolder(view) {
    lateinit var tool: Tool
    lateinit var service: TextToSpeechService


    fun bind(toolChain: ToolChain, i: Int) {

        // get the tool in rendering
        this.tool = toolChain.get(i)

        // recast service and put it in a var
        this.service = tool.service as TextToSpeechService

        // change UI tool title
        itemView.tool_title.text = tool.title



        // input and output default values & colors
        // input value
        if(tool.input == null || tool.input == "") { // We show "nothing." if the string is null or == ""
            itemView.input_value.text = "Nothing."
            itemView.input_value.setTextColor(this.view.context.resources.getColor(R.color.red))
        } else {
            itemView.input_value.text = tool.input
            itemView.input_value.setTextColor(this.view.context.resources.getColor(R.color.default_in_out))
        }

        // output value
        if(tool.output == null || tool.output == "") { // We show "nothing." if the string is null or == ""
            itemView.output_value.text = "Nothing."
            itemView.output_value.setTextColor(this.view.context.resources.getColor(R.color.red))
        } else {
            itemView.output_value.text = tool.output
            itemView.output_value.setTextColor(this.view.context.resources.getColor(R.color.default_in_out))
        }





        /** Spinners init **/
        // create adapter's array & sort locales by displayLanguage
        var localesLanguagesUI = mutableListOf<String>()
        var localesLanguagesObjects = mutableListOf<Locale>()

        // create language_spinner
        Locale.getAvailableLocales().forEach {
            // this.serviceTTS.speaker.availableLanguages can be used for android > 5.0
            if (it.country == "") { // remove duplicated languages when this one is used in different countries (it just take the most basic one) && check that the speaker support this language
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
        val adapter = ArrayAdapter(this.view.context, android.R.layout.simple_spinner_item, localesLanguagesUI)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // populate language_spinner
        view.tool_tts_language_spinner.adapter = adapter

        // set default language_spinner value
        view.tool_tts_language_spinner.setSelection(adapter.getPosition(Locale.getDefault().displayLanguage))



        /** Spinners listeners **/
        // tool_tts_language_spinner on item selected listener
        view.tool_tts_language_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                service.locale = localesLanguagesObjects[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        // tool_tts_play_button listener
        view.tool_tts_play_button.setOnClickListener {
            this.service.input = tool.input
            this.service.run()
        }
    }
}