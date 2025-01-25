package org.emberstudios.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.io.Resource
import org.emberstudios.core.io.ResourceManager
import org.emberstudios.core.logger.exitError
import org.emberstudios.core.logger.getLogger
import org.emberstudios.core.renderer.GraphicsAPIType
import org.emberstudios.renderer.opengl.GLTexture
import java.io.File

interface Texture : Resource {

    companion object {
        private val LOGGER = KotlinLogging.logger("TextureFactory")

        fun create(filepath: String): Texture = when (Renderer.apiType) {
            GraphicsAPIType.OPEN_GL -> GLTexture(filepath)
            GraphicsAPIType.VULKAN -> LOGGER.exitError { "Vulkan is not supported!" }
        }
    }

    val texID: Int
    var width: Int
    var height: Int
    var uv: FloatArray
    val filepath: String

    fun subTexture(x: Int, y: Int, width: Int, height: Int): Texture

    fun bind()
    fun unbind()

    fun activate(slot: Int = 0)
    fun deactivate(slot: Int = 0)

}

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