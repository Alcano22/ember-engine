package org.emberstudios.core.math

import org.joml.*

fun Vector2i.toArray() = intArrayOf(x, y)
fun Vector3i.toArray() = intArrayOf(x, y, z)
fun Vector4i.toArray() = intArrayOf(x, y, z, w)

fun Vector2f.toArray() = floatArrayOf(x, y)
fun Vector3f.toArray() = floatArrayOf(x, y, z)
fun Vector4f.toArray() = floatArrayOf(x, y, z, w)

fun Vector2i.toSimpleString() = "($x, $y)"
fun Vector3i.toSimpleString() = "($x, $y, $z)"
fun Vector4i.toSimpleString() = "($x, $y, $z, $w)"

fun Vector2f.toSimpleString() = "($x, $y)"
fun Vector3f.toSimpleString() = "($x, $y, $z)"
fun Vector4f.toSimpleString() = "($x, $y, $z, $w)"
