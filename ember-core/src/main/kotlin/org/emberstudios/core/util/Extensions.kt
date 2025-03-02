package org.emberstudios.core.util

import kotlin.math.abs

// General
fun String.toDisplayStyle() = this
    .replace(Regex("([a-z])([A-Z])"), "$1 $2")
    .replace("_", " ")
    .split(" ")
    .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }

fun String.toSnakeCase() = this
    .replace(Regex("([a-z])([A-Z])"), "$1_$2")
    .lowercase()

fun Float.coerceIn01() = this.coerceIn(0f, 1f)
fun Float.approx(other: Float, epsilon: Float = 1e-6f) = abs(this - other) < epsilon
