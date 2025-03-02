package org.emberstudios.engine.util

import imgui.ImVec2
import imgui.ImVec4
import org.joml.Vector2f
import org.joml.Vector4f

fun Vector2f.toImVec() = ImVec2(x, y)
fun Vector4f.toImVec() = ImVec4(x, y, z, w)