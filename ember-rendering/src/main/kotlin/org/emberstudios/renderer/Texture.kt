package org.emberstudios.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.io.Resource
import org.emberstudios.core.io.ResourceManager
import org.emberstudios.core.logger.exitError
import org.emberstudios.renderer.opengl.GLTexture

interface Texture : Resource {

    companion object {
        private val LOGGER = KotlinLogging.logger("TextureFactory")

        fun create(filepath: String): Texture = when (Renderer.apiType) {
            RenderAPIType.OPEN_GL -> GLTexture(filepath)
            RenderAPIType.VULKAN -> LOGGER.exitError { "Vulkan is not supported!" }
        }
    }

    fun bind()
    fun unbind()

    fun getID(): Int
    fun getWidth(): Int
    fun getHeight(): Int

}

fun ResourceManager.loadTexture(path: String) = load(path) { Texture.create(it) }