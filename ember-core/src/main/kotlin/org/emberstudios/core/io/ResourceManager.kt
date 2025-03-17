package org.emberstudios.core.io

import org.emberstudios.core.logger.exitError
import org.emberstudios.core.logger.getLogger
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset

/**
 * Manages resources such as text files, binary files, and fonts.
 */
object ResourceManager {

    private val LOGGER = getLogger<ResourceManager>()

    private val cache = mutableMapOf<String, Resource>()

    /**
     * Clears the cache for the specified file.
     *
     * @param filepath The path to the file.
     */
    fun clearCache(filepath: String) { cache.remove(filepath) }

    /**
     * Loads a resource from the specified file path.
     *
     * @param filepath The path to the file.
     * @param loader The loader function that creates the resource.
     *
     * @return A pair containing the resource and a boolean indicating whether the resource was cached.
     */
    fun <T : Resource> load(filepath: String, loader: (String) -> T): Pair<T, Boolean> {
        val cached = cache.containsKey(filepath)
        val resource: T
        if (!cached) {
            resource = loader(filepath)
            cache[filepath] = loader(filepath)
        } else
            resource = cache[filepath] as T
        return Pair(resource, cached)
    }

    /**
     * Loads a text file from the specified file path.
     *
     * @param filepath The path to the file.
     * @param charset The character set to use when reading the file.
     *
     * @return The content of the text file.
     */
    fun loadTextFile(filepath: String, charset: Charset = Charsets.UTF_8): String {
        return load(filepath) {
            val content = loadRawText(it, charset)
            val resource = TextResource(content)
            LOGGER.trace { "Loaded text file: '$it'" }
            resource
        }.first.text
    }

    /**
     * Loads a binary file from the specified file path.
     *
     * @param filepath The path to the file.
     *
     * @return The content of the binary file.
     */
    fun loadBinaryFile(filepath: String): ByteArray {
        return load(filepath) {
            val data = loadRawBinary(it)
            val resource = BinaryResource(data)
            LOGGER.trace { "Loaded binary file: '$it'" }
            resource
        }.first.data
    }

    /**
     * Loads a font from the specified file path.
     *
     * @param filepath The path to the file.
     *
     * @return The content of the font file.
     */
    fun loadFont(filepath: String) = ByteBuffer.wrap(loadBinaryFile(filepath))

    private fun loadRawText(filepath: String, charset: Charset): String {
        val file = File(filepath)
        return if (file.exists())
            file.readText(charset)
        else {
            getResourceStream(filepath)?.use {
                it.reader(charset).readText()
            } ?: LOGGER.exitError { "Resource not found: '$filepath'" }
        }
    }

    private fun loadRawBinary(filepath: String): ByteArray {
        val file = File(filepath)
        return if (file.exists())
            file.readBytes()
        else
            getResourceStream(filepath)?.use { it.readBytes() }
                ?: LOGGER.exitError { "Resource not found: $filepath" }
    }

    /**
     * Checks if a file exists.
     *
     * @param filepath The path to the file.
     *
     * @return True if the file exists, false otherwise.
     */
    fun exists(filepath: String) =
        File(filepath).exists() ||
        javaClass.classLoader.getResource(filepath) != null

    /**
     * Checks if a file is a text file.
     *
     * @param file The file to check.
     * @param sampleSize The number of bytes to sample from the file.
     *
     * @return True if the file is a text file, false otherwise.
     */
    fun isTextFile(file: File, sampleSize: Int = 512): Boolean {
        val bytes = file.readBytes().take(sampleSize)
        return bytes.all {
            it in 32..126 || it == 9.toByte() || it == 10.toByte() || it == 13.toByte()
        }
    }

    /**
     * Cleans up the resources.
     */
    fun cleanup() {
        cache.values.forEach {
            it.delete()
            LOGGER.trace { "Destroyed ${it::class.simpleName}" }
        }
        cache.clear()
    }

    /**
     * Gets the input stream for the specified file path.
     *
     * @param filepath The path to the file.
     *
     * @return The input stream for the file.
     */
    fun getResourceStream(filepath: String): InputStream? =
        javaClass.classLoader.getResourceAsStream(filepath)

}

/**
 * Represents a resource.
 */
interface Resource {
    /**
     * Deletes the resource.
     */
    fun delete()
}

/**
 * Represents a text resource.
 *
 * @param text The text content.
 */
class TextResource(val text: String) : Resource {
    /**
     * Deletes the resource.
     */
    override fun delete() {}
}

/**
 * Represents a binary resource.
 */
class BinaryResource(val data: ByteArray) : Resource {
    /**
     * Deletes the resource.
     */
    override fun delete() {}
}