package fr.enssat.babelblock.jourdren_duchene.services.stt

// Listener interface used to make callback for speech to text service
interface Listener {
    fun onResult(text: String, final: Boolean)
}