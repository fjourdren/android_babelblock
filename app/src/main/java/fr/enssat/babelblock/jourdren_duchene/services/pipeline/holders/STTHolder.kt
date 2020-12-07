package fr.enssat.babelblock.jourdren_duchene.services.pipeline.holders

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.enssat.babelblock.jourdren_duchene.R
import fr.enssat.babelblock.jourdren_duchene.services.pipeline.Tool
import fr.enssat.babelblock.jourdren_duchene.services.pipeline.ToolChain
import fr.enssat.babelblock.jourdren_duchene.services.stt.Listener
import fr.enssat.babelblock.jourdren_duchene.services.stt.SpeechToTextService
import fr.enssat.babelblock.jourdren_duchene.services.translation.Language
import kotlinx.android.synthetic.main.block_stt.view.*
import java.util.*

// speech to text tool holder
class STTHolder(val view: View): RecyclerView.ViewHolder(view) {
    var localesLanguagesUI = mutableListOf<String>()
    var localesLanguagesObjects = mutableListOf<Locale>()

    lateinit var tool: Tool

    // init service
    var service: SpeechToTextService = SpeechToTextService(view.context, Locale.getDefault(), object: Listener {
        override fun onResult(text: String, final: Boolean) {
            Log.d("BlockSpeechToText", "Final: $text")
            tool.output = text
            itemView.output_value.text = tool.output
            itemView.output_value.setTextColor(view.context.resources.getColor(R.color.default_in_out))
        }
    })


    fun bind(toolChain: ToolChain, i: Int) {

        // get the tool in rendering
        this.tool = toolChain.get(i)

        // change UI tool title
        itemView.tool_title.text = tool.title

        // change input & output UI
        itemView.input_value.text = tool.input
        itemView.output_value.text = tool.output

        // input and output default values & colors
        // input value
        if(tool.input == null || tool.input == "") { // We show "nothing." in red if the string is null or == ""
            itemView.input_value.text = "Nothing."
            itemView.input_value.setTextColor(this.view.context.resources.getColor(R.color.red))
        } else {
            itemView.input_value.text = tool.input
            itemView.input_value.setTextColor(this.view.context.resources.getColor(R.color.default_in_out))
        }

        // output value
        if(tool.output == null || tool.output == "") { // We show "nothing." in red if the string is null or == ""
            itemView.output_value.text = "Nothing."
            itemView.output_value.setTextColor(this.view.context.resources.getColor(R.color.red))
        } else {
            itemView.output_value.text = tool.output
            itemView.output_value.setTextColor(this.view.context.resources.getColor(R.color.default_in_out))
        }



        /** Spinner init **/
        // create adapter's array & sort locales by displayLanguage
        localesLanguagesObjects = Language.generateMLKitAvailableLocales().sortedBy { it.displayLanguage } as MutableList<Locale>

        // foreach to make localesLanguagesUI with sorted localesLanguagesObjects (need to be done in two foreach because we can't order displayLanguage without doing that)
        localesLanguagesObjects.forEach {
            localesLanguagesUI.add(it.displayLanguage) // display language in the user's default locale
        }

        // create to_language_spinner and from_language_spinner adapter
        val adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, localesLanguagesUI)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // populate tool_stt_language_spinner
        view.tool_stt_language_spinner.adapter = adapter

        // set default tool_stt_language_spinner
        view.tool_stt_language_spinner.setSelection(adapter.getPosition(this.service.locale.displayLanguage))



        /** Spinners listeners **/
        // from spinner on item selected listener
        view.tool_stt_language_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                service.locale = localesLanguagesObjects[position]
                tool.output = tool.input

                if(tool.output == null || tool.output == "") { // We show "nothing." in red if the string is null or == ""
                    itemView.output_value.text = "Nothing."
                    itemView.output_value.setTextColor(view.context.resources.getColor(R.color.red))
                } else {
                    itemView.output_value.text = tool.output
                    itemView.output_value.setTextColor(view.context.resources.getColor(R.color.default_in_out))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        // record_button listener
        view.tool_stt_record_button.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_DOWN) {
                Log.d("BlockSpeechToText", "STT button pressed")
                v.performClick()
                this.service.run()
            } else if(event.action == MotionEvent.ACTION_UP) {
                Log.d("BlockSpeechToText", "STT button released")
                this.service.stop()
            }
            false
        }
    }
}