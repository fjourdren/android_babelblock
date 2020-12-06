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
import fr.enssat.babelblock.jourdren_duchene.services.tts.TextToSpeechService
import kotlinx.android.synthetic.main.activity_pipeline.*
import kotlinx.android.synthetic.main.activity_tts.*
import java.lang.Error
import java.util.*


// inherit BaseActivity to manage menuInflater
class PipelineActivity : BaseActivity() {

    private val toolsList = arrayOf("TTS", "Translator", "STT")

    private fun getTool(ind: Int, context: Context): Tool {
        Log.d("deb", ind.toString())
        when(ind) {
            0 -> return object: Tool {
                override var title = toolsList[ind]

                override var input  = ""
                override var output = ""


                override var service: Any = TextToSpeechService(context, Locale.FRENCH)

                // override run method of Tool interface
                override fun run(input: String, output: (String) -> Unit) {
                    (this.service as TextToSpeechService).input = input // send text to the service
                    (this.service as TextToSpeechService).run()
                }

                override fun close() {
                    Log.d(title, "close")
                }
            }
            1 -> return object: Tool {
                    override var title = toolsList[ind]

                    override var input  = ""
                    override var output = ""


                    override var service: Any = TranslatorService(context, Locale.FRENCH, Locale.ENGLISH)

                    // override run method of Tool interface
                    override fun run(input: String, output: (String) -> Unit) {
                        (this.service as TranslatorService).run(this.input) { enText ->
                            Log.d("output: ", enText)
                            output(enText)
                        }
                    }

                    override fun close() {
                        Log.d(title, "close")
                    }
            }
            else -> { // Note the block
                Log.e("Error", "Not a valid service id")
                throw Error("Not a valid service id")
            }
        }

        throw Error("Not a valid service id")
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



/*

        0 -> object: Tool {
                override var title = toolsList[ind]

                override var input  = ""
                override var output = ""


                override lateinit var service: Service

                init {
                    Log.d("test", ind.toString())
                    when(ind) {
                        0 -> {
                            service = TextToSpeechService(context, Locale.FRENCH)
                        }
                        1 -> {
                            service = TranslatorService(context, Locale.FRENCH, Locale.ENGLISH)
                        }
                        else -> { // Note the block
                            Log.e("Error", "Not a valid service id")
                        }
                    }
                }

                // override run method of Tool interface
                override fun run(input: String, output: (String) -> Unit) {
                    Log.d("RUNNN", "RUN")

                    when(ind) {
                        0 -> { // text to speech
                            this.service.input = input // send text to the service
                            (this.service as TextToSpeechService).run()
                        }
                        1 -> { // translate
                            service.run(this.input) { enText ->
                                Log.d("output: ", enText)
                                output(enText)
                            }
                        }
                        else -> { // Note the block
                            Log.e("Error", "Not a valid service id")
                        }
                    }*/
