package org.emberstudios.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.io.Resource
import org.emberstudios.core.io.ResourceManager
import org.emberstudios.core.logger.exitError
import org.emberstudios.core.logger.getLogger
import org.emberstudios.core.renderer.GraphicsAPIType
import org.emberstudios.renderer.opengl.GLTexture
import java.io.File

/**
 * Texture interface
 */
interface Texture : Resource {

    companion object {
        private val LOGGER = KotlinLogging.logger("TextureFactory")

        /**
         * Create a texture from a file
         *
         * @param filepath The path to the texture file
         *
         * @return The created texture
         */
        fun create(filepath: String): Texture = when (Renderer.apiType) {
            GraphicsAPIType.OPEN_GL -> GLTexture(filepath)
            GraphicsAPIType.VULKAN -> LOGGER.exitError { "Vulkan is not supported!" }
        }
    }

    /**
     * The texture ID
     */
    val texID: Int

    /**
     * The texture width
     */
    var width: Int

    /**
     * The texture height
     */
    var height: Int

    /**
     * The texture UV coordinates
     */
    var uv: FloatArray

    /**
     * The texture filepath
     */
    val filepath: String

    /**
     * Cuts a subtexture from the texture
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @param width The width
     * @param height The height
     */
    fun subTexture(x: Int, y: Int, width: Int, height: Int): Texture

    /**
     * Binds the texture
     */
    fun bind()

    /**
     * Unbinds the texture
     */
    fun unbind()

    /**
     * Activates the texture
     *
     * @param slot The texture slot
     */
    fun activate(slot: Int = 0)

    /**
     * Deactivates the texture
     *
     * @param slot The texture slot
     */
    fun deactivate(slot: Int = 0)

}

/**
 * Load a texture from a file
 *
 * @param filepath The path to the texture file
 *
 * @return The loaded texture
 */
fun ResourceManager.loadTexture(filepath: String): Texture {
    val (texture, cached) = load(filepath) {
        if (exists(it))
            Texture.create(it)
        else
            ResourceManager.loadTexture("textures/unknown.jpg")
    }
    if (!cached)
        getLogger<ResourceManager>().trace { "Loaded texture: '$filepath'" }
    return texture
}