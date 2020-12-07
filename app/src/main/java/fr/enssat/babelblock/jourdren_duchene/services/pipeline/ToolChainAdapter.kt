package fr.enssat.babelblock.jourdren_duchene.services.pipeline

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import fr.enssat.babelblock.jourdren_duchene.R
import fr.enssat.babelblock.jourdren_duchene.services.stt.Listener
import fr.enssat.babelblock.jourdren_duchene.services.stt.SpeechToTextService
import fr.enssat.babelblock.jourdren_duchene.services.text.TextService
import fr.enssat.babelblock.jourdren_duchene.services.translation.Language
import fr.enssat.babelblock.jourdren_duchene.services.translation.TranslatorService
import fr.enssat.babelblock.jourdren_duchene.services.tts.TextToSpeechService

import kotlinx.android.synthetic.main.block_stt.view.*
import kotlinx.android.synthetic.main.block_stt.view.input_value
import kotlinx.android.synthetic.main.block_stt.view.output_value
import kotlinx.android.synthetic.main.block_stt.view.tool_title

import kotlinx.android.synthetic.main.block_translate.view.*

import kotlinx.android.synthetic.main.block_tts.view.*
import kotlinx.android.synthetic.main.block_tts.view.tool_tts_language_spinner

import kotlinx.android.synthetic.main.block_text.view.*

import java.util.*


class ToolChainAdapter: Adapter<ViewHolder>, ItemMoveAdapter {

    override var itemsChain: ToolChain
    var context: Context

    constructor(context: Context, items: ToolChain): super() {
        this.context = context
        this.itemsChain = items

        //notifyDataSetChanged() = redraw, the data set has changed
        this.itemsChain.setOnChangeListener { notifyDataSetChanged() }
    }

    override fun getItemCount(): Int = this.itemsChain.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View
        Log.d("viewType", viewType.toString())
        when(viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.block_tts, parent, false)
                return TTSHolder(view)
            }
            1 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.block_translate, parent, false)
                return TranslateHolder(view)
            }
            2 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.block_stt, parent, false)
                return STTHolder(view)
            }
            3 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.block_text, parent, false)
                return TextHolder(view)
            }
            else -> { // Note the block
                Log.e("Error", "Not a valid ViewType")
                throw Error("Not a valid ViewType")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(this.itemsChain[position].service is TextToSpeechService) {
            return 0
        } else if(this.itemsChain[position].service is TranslatorService) {
            return 1
        } else if(this.itemsChain[position].service is SpeechToTextService) {
            return 2
        } else if(this.itemsChain[position].service is TextService) {
            return 3
        }

        return -1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                (holder as TTSHolder).bind(this.itemsChain, position)
            }
            1 -> {
                (holder as TranslateHolder).bind(this.itemsChain, position)
            }
            2 -> {
                (holder as STTHolder).bind(this.itemsChain, position)
            }
            3 -> {
                (holder as TextHolder).bind(this.itemsChain, position)
            }
            else -> { // Note the block
                Log.e("Error", "Not a valid HolderType")
            }
        }
    }

    override fun onRowMoved(from: Int, to: Int) {
        this.itemsChain.move(from, to)
        notifyItemMoved(from, to)
    }

    override fun onRowDeleted(target: Int) {
        this.itemsChain.remove(target)
        notifyDataSetChanged()
    }

    override fun onRowSelected(viewHolder: ViewHolder) {
        viewHolder.itemView.setBackgroundColor(Color.GRAY)
    }

    override fun onRowReleased(viewHolder: ViewHolder) {
        viewHolder.itemView.setBackgroundColor(this.context.resources.getColor(R.color.block_color))
    }

    override fun onRowRestore(position: Int, item: Tool) {
        this.itemsChain.addAt(position, item)
        notifyItemInserted(position);
    }



    // viewholder, kind of reusable view cache, for each tool in the chain
    class TTSHolder(val view: View): ViewHolder(view) {
        lateinit var tool: Tool
        lateinit var service: TextToSpeechService

        fun bind(toolChain: ToolChain, i: Int) {

            // get the tool in rendering
            this.tool = toolChain.get(i)
            this.service = tool.service as TextToSpeechService


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


            // from spinner on item selected listener
            view.tool_tts_language_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    service.locale = localesLanguagesObjects[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // another interface callback
                }
            }


            // tool_tts_play_button listener
            view.tool_tts_play_button.setOnClickListener {
                this.service.input = tool.input
                this.service.run()
            }
        }
    }





    // viewholder, kind of reusable view cache, for each tool in the chain
    class TranslateHolder(val view: View): ViewHolder(view) {
        var localesLanguagesUI = mutableListOf<String>()
        var localesLanguagesObjects = mutableListOf<Locale>()

        lateinit var tool: Tool
        lateinit var serviceTranslator: TranslatorService

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



    // viewholder, kind of reusable view cache, for each tool in the chain
    class STTHolder(val view: View): ViewHolder(view) {
        var localesLanguagesUI = mutableListOf<String>()
        var localesLanguagesObjects = mutableListOf<Locale>()

        lateinit var tool: Tool
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


            // rendering tool title management
            itemView.tool_title.text = tool.title


            itemView.input_value.text = tool.input
            itemView.output_value.text = tool.output




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
            view.tool_stt_language_spinner.adapter = adapter

            // set default from_language_spinner
            view.tool_stt_language_spinner.setSelection(adapter.getPosition(this.service.locale.displayLanguage))


            // from spinner on item selected listener
            view.tool_stt_language_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    service.locale = localesLanguagesObjects[position]
                    tool.output = tool.input

                    if(tool.output == null || tool.output == "") { // We show "nothing." if the string is null or == ""
                        itemView.output_value.text = "Nothing."
                        itemView.output_value.setTextColor(view.context.resources.getColor(R.color.red))
                    } else {
                        itemView.output_value.text = tool.output
                        itemView.output_value.setTextColor(view.context.resources.getColor(R.color.default_in_out))
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // another interface callback
                }
            }


            // record_button listener
            view.tool_stt_record_button.setOnTouchListener { v, event ->
                if(event.action == MotionEvent.ACTION_DOWN) {
                    Log.d("SpeechToTextActivity UI", "STT button pressed")
                    v.performClick()
                    this.service.run()
                } else if(event.action == MotionEvent.ACTION_UP) {
                    Log.d("SpeechToTextActivity UI", "STT button released")
                    this.service.stop()
                }
                false
            }
        }
    }



    // viewholder, kind of reusable view cache, for each tool in the chain
    class TextHolder(val view: View): ViewHolder(view) {
        lateinit var tool: Tool
        lateinit var service: TextService

        fun bind(toolChain: ToolChain, i: Int) {

            // get the tool in rendering
            this.tool = toolChain.get(i)

            this.service = tool.service as TextService


            // rendering tool title management
            itemView.tool_title.text = tool.title


            itemView.block_text_input_value.setText(tool.input)
            itemView.output_value.text = tool.output




            // rendering input and output fields management
            if(tool.input == null || tool.input == "") { // We show "nothing." if the string is null or == ""
                itemView.output_value.text = "Nothing."
                itemView.output_value.setTextColor(this.view.context.resources.getColor(R.color.red))
            } else {
                itemView.block_text_input_value.setText(tool.input)
                itemView.block_text_input_value.setTextColor(this.view.context.resources.getColor(R.color.default_in_out))

                itemView.output_value.text = tool.output
                itemView.output_value.setTextColor(this.view.context.resources.getColor(R.color.default_in_out))
            }





            itemView.block_text_input_value.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if(s.toString() != "Nothing.") {
                        tool.input = s.toString()
                    } else {
                        return
                    }

                    if(tool.input == null || tool.input == "") {
                        itemView.output_value.text = "Nothing."
                        itemView.output_value.setTextColor(view.context.resources.getColor(R.color.red))
                    } else {
                        itemView.output_value.text = tool.input
                        itemView.block_text_input_value.setTextColor(view.context.resources.getColor(R.color.default_in_out))
                        itemView.output_value.setTextColor(view.context.resources.getColor(R.color.default_in_out))
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

        }
    }
}