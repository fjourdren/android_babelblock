package fr.enssat.babelblock.jourdren_duchene.services.pipeline

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.enssat.babelblock.jourdren_duchene.R
import fr.enssat.babelblock.jourdren_duchene.services.translation.Language
import fr.enssat.babelblock.jourdren_duchene.services.translation.TranslatorService
import kotlinx.android.synthetic.main.activity_translator.*
import kotlinx.android.synthetic.main.block_translate.view.*
import java.util.*


class ToolChainAdapter: RecyclerView.Adapter<ToolChainAdapter.ToolViewHolder>, ItemMoveAdapter {

    override var itemsChain: ToolChain
    var context: Context

    constructor(context: Context, items: ToolChain): super() {
        this.context = context
        this.itemsChain = items

        //notifyDataSetChanged() = redraw, the data set has changed
        this.itemsChain.setOnChangeListener { notifyDataSetChanged() }
    }

    override fun getItemCount(): Int = this.itemsChain.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.block_translate, parent, false)
        return ToolViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToolViewHolder, position: Int) {
        holder.bind(this.itemsChain, position)
    }

    override fun onRowMoved(from: Int, to: Int) {
        this.itemsChain.move(from, to)
        notifyItemMoved(from, to)
    }

    override fun onRowDeleted(target: Int) {
        this.itemsChain.remove(target)
        notifyDataSetChanged()
    }

    override fun onRowSelected(viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.setBackgroundColor(Color.GRAY)
    }

    override fun onRowReleased(viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.setBackgroundColor(this.context.resources.getColor(R.color.block_color))
    }

    override fun onRowRestore(position: Int, item: Tool) {
        this.itemsChain.addAt(position, item)
        notifyItemInserted(position);
    }





    // viewholder, kind of reusable view cache, for each tool in the chain
    class ToolViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        var localesLanguagesUI = mutableListOf<String>()
        var localesLanguagesObjects = mutableListOf<Locale>()

        lateinit var tool: Tool
        lateinit var serviceTranslator: TranslatorService

        var nbSpinnerNotify = 0 // fix to not call spinner's listener when there is a notifyDataSetChanged()

        fun bind(toolChain: ToolChain, i: Int) {

            // get the tool in rendering
            this.tool = toolChain.get(i)
            this.serviceTranslator = tool.service as TranslatorService


            // rendering tool title management
            itemView.tool_title.text = tool.title




            // rendering input and output fields management
            if(tool.input == null || tool.input == "") { // We show "nothing." if the string is null or == ""
                itemView.input_value.text = "Nothing."
                itemView.input_value.setTextColor(this.view.context.resources.getColor(R.color.red))
            } else {
                itemView.input_value.text = tool.input
                itemView.input_value.setTextColor(this.view.context.resources.getColor(R.color.default_in_out))
            }

            if(tool.output == null || tool.output == "") { // We show "nothing." if the string is null or == ""
                itemView.output_value.text = "Nothing."
                itemView.output_value.setTextColor(this.view.context.resources.getColor(R.color.red))
            } else {
                itemView.output_value.text = tool.output
                itemView.output_value.setTextColor(this.view.context.resources.getColor(R.color.default_in_out))
            }





            // Spinner management
            /* === SPINNERS INIT === */
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
            from_language_spinner.setSelection(adapter.getPosition(this.serviceTranslator.from_language.displayLanguage))



            // populate to_language_spinner
            val to_language_spinner = view.tool_translate_to_language_spinner
            to_language_spinner.adapter = adapter

            // set default to_language_spinner
            to_language_spinner.setSelection(adapter.getPosition(this.serviceTranslator.to_language.displayLanguage))



            // language spinners listeners

            // from spinner on item selected listener
            from_language_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val old_value = serviceTranslator.from_language
                    serviceTranslator.from_language = localesLanguagesObjects[position]

                    // manage UI & download model
                    downloadTranslationModel()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // another interface callback
                }
            }




            // to spinner on item selected listener
            to_language_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val old_value = serviceTranslator.to_language
                    serviceTranslator.to_language = localesLanguagesObjects[position]

                    // manage UI & download model
                    downloadTranslationModel()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // another interface callback
                }
            }
        }


        fun downloadTranslationModel() {
            itemView.tool_translate_info_message.text = "Downloading translation model... Please wait."
            // force reseting output value when we change value
            itemView.output_value.text = ""
            tool.output = ""
            this.serviceTranslator.output = ""

            this.serviceTranslator.downloadModelIfNeeded({
                itemView.tool_translate_info_message.text = "State: Ready."
            }, {
                itemView.tool_translate_info_message.text = "Error: Model download failed, retrying..."
            }, {
                itemView.tool_translate_info_message.text = "Error: App can't download the translation model, please check your wifi connection or used languages..."
            })
        }
    }
}