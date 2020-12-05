package fr.enssat.babelblock.jourdren_duchene

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ArrayAdapter
import fr.enssat.babelblock.jourdren_duchene.services.pipeline.*
import kotlinx.android.synthetic.main.activity_pipeline.*

// inherit BaseActivity to manage menuInflater
class PipelineActivity : BaseActivity() {

    val handler = Handler(Looper.getMainLooper())

    private val toolsList = arrayOf("STT", "Translator", "TTS")

    private fun getTool(ind: Int) = object: ToolDisplay {
                override var title = toolsList[ind]

                override var output = ""
                override var input  = ""

                override val tool   = object : Tool {
                    // override run method of Tool interface
                    override fun run(input: String, output: (String) -> Unit) {
                        handler.postDelayed({output("$input $ind")},1000)
                    }

                    override fun close() {
                        Log.d(title, "close")
                    }
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
            toolChain.add(getTool(position))
        }

    }
}