package fr.enssat.babelblock.jourdren_duchene.services.pipeline

import fr.enssat.babelblock.jourdren_duchene.services.Service

interface Tool {
    var title: String
    var input: String
    var output: String

    var service: Service

    fun run(input: String, callback: (String) -> Unit)
    fun close()
}

class ToolChain(list: List<Tool> = emptyList()) {

    private val list = list.toMutableList()
    val size
        get() = list.size

    private var onChangeListener: (() -> Unit)? = null

    // callback to invoke void method on toolchain Changes (see init of ToolChainAdapter)
    fun setOnChangeListener(callback: () -> Unit) {
        onChangeListener = callback
    }

    fun add(tool: Tool) {
        list.add(tool)
        onChangeListener?.invoke()
    }

    // add at a specific location
    fun addAt(index: Int, tool: Tool) {
        list.add(index, tool)
        onChangeListener?.invoke()
    }

    operator fun get(index: Int) = list.get(index)

    // remove and insert
    fun move(from: Int, to: Int) {
        val dragged = list.removeAt(from)
        list.add(to, dragged)
    }

    // delete tool
    fun remove(from: Int) {
        list.removeAt(from)
    }

    // display each input/output of this chain starting at the given position with an initial empty input
    fun display(position: Int, input: String = "") {
        //recursive loop
        fun loop(value: String, chain: List<Tool>) {
            // if not null do the let statement test end of recursion
            chain.firstOrNull()?.let {
                it.input = value
                onChangeListener?.invoke()

                it.run(value) { output ->
                    it.output = output
                    onChangeListener?.invoke()

                    //loop on the remaining chain
                    loop(output, chain.drop(1))
                }
            }
        }

        // start recursion
        loop(input, list.drop(position))
    }
}