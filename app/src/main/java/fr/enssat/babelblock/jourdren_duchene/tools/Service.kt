package fr.enssat.babelblock.jourdren_duchene.tools

interface SpeechToTextTool {
    interface Listener {
        fun onResult(text: String, isFinal: Boolean)
    }
}