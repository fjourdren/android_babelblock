package fr.enssat.babelblock.jourdren_duchene

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import fr.enssat.babelblock.jourdren_duchene.services.Service
import fr.enssat.babelblock.jourdren_duchene.services.pipeline.Tool
import fr.enssat.babelblock.jourdren_duchene.services.pipeline.ToolChain
import fr.enssat.babelblock.jourdren_duchene.services.pipeline.ToolChainAdapter
import fr.enssat.babelblock.jourdren_duchene.services.pipeline.ToolChainMoveSwipeHelper
import fr.enssat.babelblock.jourdren_duchene.services.translation.TranslatorService
import kotlinx.android.synthetic.main.activity_pipeline.*
import java.util.*


// inherit BaseActivity to manage menuInflater
class PipelineActivity : BaseActivity() {

    private val toolsList = arrayOf("STT", "Translator", "TTS")

    private fun getTool(ind: Int, context: Context) = object: Tool {
                override var title = toolsList[ind]

                override var input  = ""
                override var output = ""

                override var service: Service = TranslatorService(context, Locale.FRENCH, Locale.ENGLISH)

                // override run method of Tool interface
                override fun run(input: String, output: (String) -> Unit) {
                    Log.d("RUNNN", "RUN")
                    service.run(this.input) { enText ->
                        Log.d("output: ", enText)
                        output(enText)
                    }
                }

                override fun close() {
                    Log.d(title, "close")
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pipeline)

        // create toolchain and its adapter
        val toolChain = ToolChain()
        val adapter = ToolChainAdapter(this, toolChain)

        // dedicated drag and drop mover helper
        val moveHelper = ToolChainMoveSwipeHelper.create(adapter)
        moveHelper.attachToRecyclerView(tool_chain_list)

        // see tool_chain_list in activity_tool_chain.xml (tools chain)
        tool_chain_list.adapter = adapter

        // see tool_list in activity_tool_chain.xml (simple ids list)
        tool_list.adapter = ArrayAdapter(this, R.layout.simple_text_view, toolsList)
        tool_list.setOnItemClickListener { _, _, position, _ ->
            toolChain.add(getTool(position, this))
        }


        // run button on click
        pipeline_play_button.setOnClickListener {
            toolChain.display(0, "Bonjour le monde")
        }
    }
}