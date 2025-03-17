package org.emberstudios.renderer

import org.emberstudios.core.io.ResourceManager
import org.emberstudios.core.logger.exitError
import org.emberstudios.core.logger.getLogger

/**
 * A sprite sheet is a collection of sprites in a single image.
 *
 * @param filepath The path to the sprite sheet image.
 * @param cellWidth The width of each cell in the sprite sheet.
 * @param cellHeight The height of each cell in the sprite sheet.
 */
class SpriteSheet(
    val filepath: String,
    private val cellWidth: Int,
    private val cellHeight: Int
) {
    companion object {
        private val LOGGER = getLogger<SpriteSheet>()
    }

    private val texture = ResourceManager.loadTexture(filepath)
    private val cols = texture.width / cellWidth
    private val rows = texture.height / cellHeight

    private val cache = mutableMapOf<Pair<Int, Int>, Texture>()

    /**
     * Get a sprite from the sprite sheet.
     *
     * @param x The x index of the sprite.
     * @param y The y index of the sprite.
     *
     * @return The sprite at the given index.
     */
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

    /**
     * Get a range of sprites from the sprite sheet.
     *
     * @param x The x index of the first sprite.
     * @param y The y index of the first sprite.
     * @param count The number of sprites to get.
     *
     * @return An array of sprites starting at the given index.
     */
    fun getRange(x: Int, y: Int, count: Int) = Array<Texture>(count) { get(x + it, y)!! }
}