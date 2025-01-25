package org.emberstudios.engine.util

import kotlin.math.max
import kotlin.math.min

fun clamp(value: Float, min: Float, max: Float) = max(min, min(max, value))
fun clamp01(value: Float) = clamp(value, 0f, 1f)