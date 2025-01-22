package org.emberstudios.renderer

import org.emberstudios.core.logger.exitError
import org.emberstudios.core.logger.getLogger

class SpriteSheet(
    val filepath: String,
    private val cellWidth: Int,
    private val cellHeight: Int
) {
    companion object {
        val LOGGER = getLogger<SpriteSheet>()
    }

    private val texture = Texture.create(filepath)
    private val cols = texture.width / cellWidth
    private val rows = texture.height / cellHeight

    private val cache = mutableMapOf<Pair<Int, Int>, Texture>()

    operator fun get(x: Int, y: Int): Texture? {
        if (x !in 0..<cols && y !in 0..<rows) {
            LOGGER.error { "Sprite index out of bounds: ($x, $y) max: ($cols, $rows)" }
            return null
        }

        return cache.getOrPut(x to y) {
            val px = x * cellWidth
            val py = y * cellWidth
            texture.subTexture(px, py, cellWidth, cellHeight)
        }
    }
}