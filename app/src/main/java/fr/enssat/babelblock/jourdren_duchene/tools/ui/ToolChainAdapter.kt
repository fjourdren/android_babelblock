package fr.enssat.babelblock.jourdren_duchene.tools.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.enssat.babelblock.jourdren_duchene.R
import kotlinx.android.synthetic.main.list_item_tool_chain.view.*

class ToolChainAdapter(val context: Context, val toolChain: ToolChain) : RecyclerView.Adapter<ToolChainAdapter.ToolViewHolder>(), ItemMoveAdapter {

    init {
        //notifyDataSetChanged() = redraw, the data set has changed
        toolChain.setOnChangeListener { notifyDataSetChanged() }
    }

    override fun getItemCount(): Int = toolChain.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_tool_chain, parent, false)
        return ToolViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToolViewHolder, position: Int) {
        holder.bind(toolChain, position)
    }

    override fun onRowMoved(from: Int, to: Int) {
        toolChain.move(from, to)
        notifyItemMoved(from, to)
    }

    override fun onRowDeleted(target: Int) {
        toolChain.remove(target)
        notifyDataSetChanged()
    }

    override fun onRowSelected(viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.setBackgroundColor(Color.GRAY)
    }

    override fun onRowReleased(viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.setBackgroundColor(this.context.resources.getColor(R.color.block_color))
    }

    // viewholder, kind of reusable view cache, for each tool in the chain
    class ToolViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(toolChain: ToolChain, i: Int) {

            // get the tool in rendering
            val tool = toolChain.get(i)


            // rendering input and output fields management
            if(tool.input == null) { // We show "nothing." if the string is null (if string is == "" => we don't show anything)
                itemView.input_value.text = "Nothing."
                itemView.input_value.setTextColor(this.view.context.resources.getColor(R.color.red))
            } else {
                itemView.input_value.text = tool.input
                itemView.input_value.setTextColor(this.view.context.resources.getColor(R.color.default_in_out))
            }

            if(tool.output == null) { // We show "nothing." if the string is null (if string is == "" => we don't show anything)
                itemView.output_value.text = "Nothing."
                itemView.output_value.setTextColor(this.view.context.resources.getColor(R.color.red))
            } else {
                itemView.output_value.text = tool.output
                itemView.output_value.setTextColor(this.view.context.resources.getColor(R.color.default_in_out))
            }


            // rendering tool title management
            itemView.params.text = tool.title


            // render tool in the pipeline
            itemView.params.setOnClickListener {
                toolChain.display(i)
            }
        }
    }
}