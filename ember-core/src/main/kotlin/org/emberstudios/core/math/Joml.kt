package org.emberstudios.core.math

import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector3f
import org.joml.Vector3i
import org.joml.Vector4f
import org.joml.Vector4i

fun Vector2i.toArray() = intArrayOf(x, y)
fun Vector3i.toArray() = intArrayOf(x, y, z)
fun Vector4i.toArray() = intArrayOf(x, y, z, w)

fun Vector2f.toArray() = floatArrayOf(x, y)
fun Vector3f.toArray() = floatArrayOf(x, y, z)
fun Vector4f.toArray() = floatArrayOf(x, y, z, w)