package org.emberstudios.core.io

import org.emberstudios.core.logger.exitError
import org.emberstudios.core.logger.getLogger
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer

object ImageLoader {

    private val LOGGER = getLogger<ImageLoader>()

    data class ImageData(
        val width: Int,
        val height: Int,
        val channels: Int,
        val data: ByteBuffer
    )

    fun load(path: String): ImageData {
        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)
            val pChannels = stack.mallocInt(1)

            val buffer = stbi_load(path, pWidth, pHeight, pChannels, 0)
                ?: LOGGER.exitError { "Failed to load image: $path" }

            return ImageData(
                pWidth.get(),
                pHeight.get(),
                pChannels.get(),
                buffer
            )
        }
    }

    fun free(data: ByteBuffer) = stbi_image_free(data)

    fun isTransparent(image: ImageData) = image.channels == 4

}