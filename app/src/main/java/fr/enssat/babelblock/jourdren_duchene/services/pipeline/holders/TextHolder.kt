package fr.enssat.babelblock.jourdren_duchene.services.pipeline.holders

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import fr.enssat.babelblock.jourdren_duchene.R
import fr.enssat.babelblock.jourdren_duchene.services.pipeline.Tool
import fr.enssat.babelblock.jourdren_duchene.services.pipeline.ToolChain
import fr.enssat.babelblock.jourdren_duchene.services.text.TextService
import kotlinx.android.synthetic.main.block_text.view.tool_title
import kotlinx.android.synthetic.main.block_text.view.block_text_input_value
import kotlinx.android.synthetic.main.block_text.view.output_value

// text tool holder (very basic one to input a value)
class TextHolder(val view: View): RecyclerView.ViewHolder(view) {
    lateinit var tool: Tool
    lateinit var service: TextService


    fun bind(toolChain: ToolChain, i: Int) {

        // get the tool in rendering
        this.tool = toolChain.get(i)

        // recast service and put it in a var
        this.service = tool.service as TextService

        // change UI tool title
        itemView.tool_title.text = tool.title

        // change input & output UI
        itemView.block_text_input_value.setText(tool.input)
        itemView.output_value.text = tool.output

        // input and output default values & colors
        if(tool.input == null || tool.input == "") { // We show "nothing." in red if the string is null or == ""
            itemView.output_value.text = "Nothing."
            itemView.output_value.setTextColor(this.view.context.resources.getColor(R.color.red))
        } else {
            itemView.block_text_input_value.setText(tool.input)
            itemView.block_text_input_value.setTextColor(this.view.context.resources.getColor(R.color.default_in_out))

            itemView.output_value.text = tool.output
            itemView.output_value.setTextColor(this.view.context.resources.getColor(R.color.default_in_out))
        }


        // on user change input value
        itemView.block_text_input_value.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                tool.input = s.toString()
                if(tool.input == null || tool.input == "") { // We show "nothing." in red if the string is null or == ""
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