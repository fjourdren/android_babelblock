package fr.enssat.babelblock.jourdren_duchene

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import fr.enssat.babelblock.jourdren_duchene.services.pipeline.*
import fr.enssat.babelblock.jourdren_duchene.services.text.TextService
import fr.enssat.babelblock.jourdren_duchene.services.stt.Listener
import fr.enssat.babelblock.jourdren_duchene.services.stt.SpeechToTextService
import fr.enssat.babelblock.jourdren_duchene.services.tts.TextToSpeechService
import kotlinx.android.synthetic.main.activity_pipeline.*
import java.lang.Error
import java.util.*


// inherit BaseActivity to manage menuInflater
class PipelineActivity : BaseActivity() {

    private val toolsList = arrayOf("TTS", "Translator", "STT", "Text")

    enum class TOOLS_TYPE(i: Int) {
        TTS(0), TRANSLATOR(1), STT(2), TEXT(3)
    }

    private fun getTool(ind: Int, context: Context): Tool {
        // get tool thanks to index in the menu
        when(TOOLS_TYPE.values()[ind]) {
            TOOLS_TYPE.TTS -> return object: Tool {
                override var title = toolsList[ind]

                override var input  = ""
                override var output = ""

                // init service
                override var service: Any = TextToSpeechService(context, Locale.getDefault())

                // run service
                override fun run(input: String, output: (String) -> Unit) {
                    (this.service as TextToSpeechService).input = input // send text to the service
                    (this.service as TextToSpeechService).run()
                    output(input) // force output to be able to put things after this tool
                }

                override fun close() {
                    (this.service as TextToSpeechService).close()
                    Log.d(title, "close")
                }
            }
            TOOLS_TYPE.TRANSLATOR -> return object: Tool {
                override var title = toolsList[ind]

                override var input  = ""
                override var output = ""

                // init service
                override var service: Any = TranslatorPipelineService(context, Locale.FRENCH, Locale.ENGLISH)

                // run service
                    override fun run(input: String, output: (String) -> Unit) {
                        (this.service as TranslatorPipelineService).run(this.input) { enText ->
                            Log.d("output: ", enText)
                            output(enText) // set output with translation
                        }
                    }

                    override fun close() {
                        (this.service as TranslatorPipelineService).close()
                        Log.d(title, "close")
                    }
            }
            TOOLS_TYPE.STT -> return object: Tool {
                override var title = toolsList[ind]

                override var input  = ""
                override var output = ""

                // init service
                override var service: Any = SpeechToTextService(context, Locale.getDefault(), object: Listener {
                    override fun onResult(text: String, final: Boolean) {}
                })

                // run service
                override fun run(input: String, output: (String) -> Unit) {
                    output(this.output)
                }

                override fun close() {
                    (this.service as SpeechToTextService).close()
                    Log.d(title, "close")
                }
            }
            TOOLS_TYPE.TEXT -> return object: Tool {
                override var title = toolsList[ind]

                override var input  = ""
                override var output = ""

                // init service
                override var service: Any = TextService(context)

                // run service
                override fun run(input: String, output: (String) -> Unit) {
                    output(this.input) // set output value
                }

                override fun close() {
                    (this.service as TextService).close()
                    Log.d(title, "close")
                }
            }
            else -> {
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


        // on click pipeline_play_button
        pipeline_play_button.setOnClickListener {
            pipeline_play_button.isEnabled = false
            pipeline_play_button.setImageResource(android.R.drawable.ic_popup_sync)
            pipeline_play_button.setColorFilter(getResources().getColor(R.color.red))

            var callbackUI = {
                pipeline_play_button.isEnabled = true
                pipeline_play_button.setImageResource(android.R.drawable.ic_media_play)
                pipeline_play_button.setColorFilter(getResources().getColor(android.R.color.holo_green_dark))
            }

            // check that pipeline isn't empty
            if(toolChain.size > 0) {
                // if first block is a text, then we force insert input value in the execution loop (easy fix)
                if (toolChain[0].service is TextService) {
                    toolChain.display(0, callbackUI, toolChain[0].input)
                } else {
                    toolChain.display(0, callbackUI) // run pipeline without text tool has a first element
                }
            } else {
                callbackUI.invoke()
            }
        }
    }
}