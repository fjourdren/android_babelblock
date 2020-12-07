package fr.enssat.babelblock.jourdren_duchene.services.pipeline

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import fr.enssat.babelblock.jourdren_duchene.PipelineActivity
import fr.enssat.babelblock.jourdren_duchene.R
import fr.enssat.babelblock.jourdren_duchene.services.pipeline.holders.STTHolder
import fr.enssat.babelblock.jourdren_duchene.services.pipeline.holders.TTSHolder
import fr.enssat.babelblock.jourdren_duchene.services.pipeline.holders.TextHolder
import fr.enssat.babelblock.jourdren_duchene.services.pipeline.holders.TranslateHolder
import fr.enssat.babelblock.jourdren_duchene.services.stt.SpeechToTextService
import fr.enssat.babelblock.jourdren_duchene.services.text.TextService
import fr.enssat.babelblock.jourdren_duchene.services.translation.TranslatorService
import fr.enssat.babelblock.jourdren_duchene.services.tts.TextToSpeechService

class ToolChainAdapter: Adapter<ViewHolder>, ItemMoveAdapter {

    override var itemsChain: ToolChain // contain list of tools
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

        // manage different type of items (we create holders)
        when(PipelineActivity.TOOLS_TYPE.values()[viewType]) { // use enum
            PipelineActivity.TOOLS_TYPE.TTS -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.block_tts, parent, false)
                return TTSHolder(view)
            }
            PipelineActivity.TOOLS_TYPE.TRANSLATOR -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.block_translate, parent, false)
                return TranslateHolder(view)
            }
            PipelineActivity.TOOLS_TYPE.STT -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.block_stt, parent, false)
                return STTHolder(view)
            }
            PipelineActivity.TOOLS_TYPE.TEXT -> {
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
        // get view type thanks to service type
        when(this.itemsChain[position].service) {
            is TextToSpeechService -> return 0
            is TranslatorService   -> return 1
            is SpeechToTextService -> return 2
            is TextService         -> return 3
        }

        return -1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // automatic bind holder with the tool in items chain
        when (PipelineActivity.TOOLS_TYPE.values()[holder.itemViewType]) { // use enum
            PipelineActivity.TOOLS_TYPE.TTS -> {
                (holder as TTSHolder).bind(this.itemsChain, position)
            }
            PipelineActivity.TOOLS_TYPE.TRANSLATOR -> {
                (holder as TranslateHolder).bind(this.itemsChain, position)
            }
            PipelineActivity.TOOLS_TYPE.STT -> {
                (holder as STTHolder).bind(this.itemsChain, position)
            }
            PipelineActivity.TOOLS_TYPE.TEXT -> {
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
}