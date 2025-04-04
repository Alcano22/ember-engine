package org.emberstudios.core.io

import org.emberstudios.core.logger.exitError
import org.emberstudios.core.logger.getLogger
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Utility class for loading images.
 */
object ImageLoader {

    private val LOGGER = getLogger<ImageLoader>()

    /**
     * Data class representing image data.
     */
    data class ImageData(
        val width: Int,
        val height: Int,
        val channels: Int,
        val data: ByteBuffer
    )

    /**
     * Load an image from a file.
     *
     * @param filepath The path to the image file.
     */
    fun load(filepath: String): ImageData {
        val buffer: ByteBuffer

        val file = File(filepath)
        if (file.exists()) {
            buffer = Files.readAllBytes(Paths.get(filepath)).let {
                ByteBuffer.allocateDirect(it.size).put(it).flip()
            }
        } else {
            val stream: InputStream = ImageLoader::class.java.classLoader.getResourceAsStream(filepath)
                ?: LOGGER.exitError { "Resource not found: '$filepath'" }

            buffer = stream.use { it.readBytes().let { bytes -> ByteBuffer.allocateDirect(bytes.size).put(bytes).flip() } }
        }

        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)
            val pChannels = stack.mallocInt(1)

            stbi_set_flip_vertically_on_load(true)
            val imageData = stbi_load_from_memory(buffer, pWidth, pHeight, pChannels, 0)
                ?: LOGGER.exitError { "Failed to load image: $filepath" }
            stbi_set_flip_vertically_on_load(false)

            return ImageData(
                pWidth.get(),
                pHeight.get(),
                pChannels.get(),
                imageData
            )
        }
    }

    /**
     * Free the memory allocated for the image data.
     */
    fun free(data: ByteBuffer) = stbi_image_free(data)

    /**
     * Check if an image is transparent.
     *
     * @param image The image data.
     * @return True if the image is transparent, false otherwise.
     */
    fun isTransparent(image: ImageData) = image.channels == 4

}