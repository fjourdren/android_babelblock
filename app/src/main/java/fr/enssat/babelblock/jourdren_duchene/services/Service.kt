package fr.enssat.babelblock.jourdren_duchene.services

import android.content.Context

abstract class Service(context: Context) {
    lateinit var context: Context;

    lateinit var input: String;
    lateinit var output: String;

    open fun run() {}
    open fun stop() {}
    open fun close() {}
}