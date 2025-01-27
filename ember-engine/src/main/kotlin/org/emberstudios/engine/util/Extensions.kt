package org.emberstudios.engine.util

import imgui.ImVec2
import imgui.ImVec4
import org.joml.*

fun String.toDisplayName() = this
    .replace(Regex("([a-z])([A-Z])"), "$1 $2")
    .replace("_", " ")
    .split(" ")
    .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }

fun Vector2f.toImVec() = ImVec2(x, y)
fun Vector4f.toImVec() = ImVec4(x, y, z, w)
