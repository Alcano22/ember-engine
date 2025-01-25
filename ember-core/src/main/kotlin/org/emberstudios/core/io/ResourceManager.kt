package org.emberstudios.core.io

import org.emberstudios.core.logger.exitError
import org.emberstudios.core.logger.getLogger
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset

object ResourceManager {

    private val LOGGER = getLogger<ResourceManager>()

    private val cache = mutableMapOf<String, Resource>()

    fun clearCache(filepath: String) { cache.remove(filepath) }

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

    fun loadTextFile(filepath: String, charset: Charset = Charsets.UTF_8): String {
        return load(filepath) {
            val content = loadRawText(it, charset)
            val resource = TextResource(content)
            LOGGER.trace { "Loaded text file: '$it'" }
            resource
        }.first.text
    }

    fun loadBinaryFile(filepath: String): ByteArray {
        return load(filepath) {
            val data = loadRawBinary(it)
            val resource = BinaryResource(data)
            LOGGER.trace { "Loaded binary file: '$it'" }
            resource
        }.first.data
    }

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

    fun exists(filepath: String) =
        File(filepath).exists() ||
        javaClass.classLoader.getResource(filepath) != null

    fun isTextFile(file: File, sampleSize: Int = 512): Boolean {
        val bytes = file.readBytes().take(sampleSize)
        return bytes.all {
            it in 32..126 || it == 9.toByte() || it == 10.toByte() || it == 13.toByte()
        }
    }

    fun cleanup() {
        cache.values.forEach {
            it.delete()
            LOGGER.trace { "Destroyed ${it::class.simpleName}" }
        }
        cache.clear()
    }

    fun getResourceStream(filepath: String): InputStream? =
        javaClass.classLoader.getResourceAsStream(filepath)

}

interface Resource {
    fun delete()
}

class TextResource(val text: String) : Resource {
    override fun delete() {}
}

class BinaryResource(val data: ByteArray) : Resource {
    override fun delete() {}
}