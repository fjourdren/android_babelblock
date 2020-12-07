package fr.enssat.babelblock.jourdren_duchene.services.pipeline.holders

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.enssat.babelblock.jourdren_duchene.R
import fr.enssat.babelblock.jourdren_duchene.services.pipeline.Tool
import fr.enssat.babelblock.jourdren_duchene.services.pipeline.ToolChain
import fr.enssat.babelblock.jourdren_duchene.services.pipeline.TranslatorPipelineService
import fr.enssat.babelblock.jourdren_duchene.services.translation.Language
import kotlinx.android.synthetic.main.block_stt.view.*
import kotlinx.android.synthetic.main.block_translate.view.*
import kotlinx.android.synthetic.main.block_translate.view.input_value
import kotlinx.android.synthetic.main.block_translate.view.output_value
import kotlinx.android.synthetic.main.block_translate.view.tool_title
import java.util.*

class TranslateHolder(val view: View): RecyclerView.ViewHolder(view) {
    var localesLanguagesUI = mutableListOf<String>()
    var localesLanguagesObjects = mutableListOf<Locale>()

    lateinit var tool: Tool
    lateinit var service: TranslatorPipelineService


    fun bind(toolChain: ToolChain, i: Int) {

        // get the tool in rendering
        this.tool = toolChain.get(i)

        // change UI tool title
        itemView.tool_title.text = tool.title

        // prepare service
        this.service = tool.service as TranslatorPipelineService

        // change input & output UI
        itemView.input_value.text = tool.input
        itemView.output_value.text = tool.output

        // rendering input and output fields management
        if(tool.input == null || tool.input == "") { // We show "nothing." in red if the string is null or == ""
            itemView.input_value.text = "Nothing."
            itemView.input_value.setTextColor(this.view.context.resources.getColor(R.color.red))
        } else {
            itemView.input_value.text = tool.input
            itemView.input_value.setTextColor(this.view.context.resources.getColor(R.color.default_in_out))
        }

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

        // populate from_language_spinner
        val from_language_spinner = view.tool_translate_from_language_spinner
        from_language_spinner.adapter = adapter

        // set default from_language_spinner
        from_language_spinner.setSelection(adapter.getPosition(this.service.from_language.displayLanguage))

        // populate to_language_spinner
        val to_language_spinner = view.tool_translate_to_language_spinner
        to_language_spinner.adapter = adapter

        // set default to_language_spinner
        to_language_spinner.setSelection(adapter.getPosition(this.service.to_language.displayLanguage))



        /** Spinners listeners **/
        // from spinner on item selected listener
        from_language_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                service.from_language = localesLanguagesObjects[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // to spinner on item selected listener
        to_language_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                service.to_language = localesLanguagesObjects[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

}