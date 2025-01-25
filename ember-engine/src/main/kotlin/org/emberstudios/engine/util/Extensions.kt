package org.emberstudios.engine.util

fun String.toDisplayName() = this
    .replace(Regex("([a-z])([A-Z])"), "$1 $2")
    .replace("_", " ")
    .split(" ")
    .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }
