package fr.enssat.babelblock.jourdren_duchene.services.block

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.enssat.babelblock.jourdren_duchene.R
import kotlinx.android.synthetic.main.list_item_tool_chain.view.*

class ToolChainAdapter: RecyclerView.Adapter<ToolChainAdapter.ToolViewHolder>, ItemMoveAdapter {

    override var items: ToolChain
    var context: Context

    constructor(context: Context, items: ToolChain): super() {
        this.context = context
        this.items = items

        //notifyDataSetChanged() = redraw, the data set has changed
        this.items.setOnChangeListener { notifyDataSetChanged() }
    }

    override fun getItemCount(): Int = this.items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_tool_chain, parent, false)
        return ToolViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToolViewHolder, position: Int) {
        holder.bind(this.items, position)
    }

    override fun onRowMoved(from: Int, to: Int) {
        this.items.move(from, to)
        notifyItemMoved(from, to)
    }

    override fun onRowDeleted(target: Int) {
        this.items.remove(target)
        notifyDataSetChanged()
    }

    override fun onRowSelected(viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.setBackgroundColor(Color.GRAY)
    }

    override fun onRowReleased(viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.setBackgroundColor(this.context.resources.getColor(R.color.block_color))
    }

    override fun onRowRestore(position: Int, item: ToolDisplay) {
        this.items.addAt(position, item)
        notifyItemInserted(position);
    }

    // viewholder, kind of reusable view cache, for each tool in the chain
    class ToolViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(toolChain: ToolChain, i: Int) {

            // get the tool in rendering
            val tool = toolChain.get(i)


            // rendering tool title management
            itemView.tool_title.text = tool.title
            itemView.params.text = tool.title


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


            // render tool in the pipeline
            itemView.params.setOnClickListener {
                toolChain.display(i)
            }
        }
    }
}