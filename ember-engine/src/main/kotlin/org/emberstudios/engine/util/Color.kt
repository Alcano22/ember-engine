package org.emberstudios.engine.util

import kotlinx.serialization.Serializable
import org.emberstudios.engine.serialization.ColorSerializer
import org.joml.Vector4f

@Serializable(with = ColorSerializer::class)
class Color(r: Float, g: Float, b: Float, a: Float = 1f) : Vector4f(r, g, b, a) {

    companion object {
        val WHITE       = Color(1f, 1f, 1f)
        val BLACK       = Color(0f, 0f, 0f)
        val RED         = Color(1f, 0f, 0f)
        val GREEN       = Color(0f, 1f, 0f)
        val BLUE        = Color(0f, 0f, 1f)
        val YELLOW      = Color(1f, 1f, 0f)
        val CYAN        = Color(0f, 1f, 1f)
        val TRANSPARENT = Color(0f, 0f, 0f, 0f)

        val IM_WHITE        = WHITE.toImVec()
        val IM_BLACK        = BLACK.toImVec()
        val IM_RED          = RED.toImVec()
        val IM_GREEN        = GREEN.toImVec()
        val IM_BLUE         = BLUE.toImVec()
        val IM_YELLOW       = YELLOW.toImVec()
        val IM_CYAN         = CYAN.toImVec()
        val IM_TRANSPARENT  = TRANSPARENT.toImVec()

        val HEX_WHITE        = WHITE.toHex()
        val HEX_BLACK        = BLACK.toHex()
        val HEX_RED          = RED.toHex()
        val HEX_GREEN        = GREEN.toHex()
        val HEX_BLUE         = BLUE.toHex()
        val HEX_YELLOW       = YELLOW.toHex()
        val HEX_CYAN         = CYAN.toHex()
        val HEX_TRANSPARENT  = TRANSPARENT.toHex()
    }

    var r: Float = r
        get() = x
        set(value) { field = clamp01(value) }
    var g: Float = g
        get() = y
        set(value) { field = clamp01(value) }
    var b: Float = b
        get() = z
        set(value) { field = clamp01(value) }
    var a: Float = w
        get() = w
        set(value) { field = clamp01(value) }

    constructor(vec4f: Vector4f) : this(vec4f.x, vec4f.y, vec4f.z, vec4f.w)

    fun setRGBA(r: Float, g: Float, b: Float, a: Float = 1f): Color {
        this.x = r
        this.y = g
        this.z = b
        this.w = a
        return this
    }

    fun toVec4f() = Vector4f(this)

    fun toHex(): Int {
        val ir = (r.coerceIn(0f, 1f) * 255).toInt()
        val ig = (g.coerceIn(0f, 1f) * 255).toInt()
        val ib = (b.coerceIn(0f, 1f) * 255).toInt()
        val ia = (a.coerceIn(0f, 1f) * 255).toInt()
        return (ia shl 24) or (ib shl 16) or (ig shl 8) or ir
    }


}