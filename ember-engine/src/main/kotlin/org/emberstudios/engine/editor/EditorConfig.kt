package org.emberstudios.engine.editor

import org.emberstudios.core.logger.exitError
import org.emberstudios.core.logger.getLogger
import java.io.File
import java.util.Properties

object EditorConfig {

    private const val FILEPATH = "editor_config.properties"

    private val properties = Properties()

    fun loadConfig() {
        val file = File(FILEPATH)
        if (file.exists())
            file.inputStream().use { properties.load(it) }
    }

    fun saveConfig() =
        File(FILEPATH).outputStream().use { properties.store(it, "Editor Configuration") }

    inline fun <reified T> get(key: String, default: T) = when (default) {
        is Boolean -> getBoolean(key, default)
        is Int -> getInt(key, default)
        is Float -> getFloat(key, default)
        is String -> getString(key, default)
        else -> getLogger<EditorConfig>().exitError { "Unsupported type: ${T::class.simpleName!!}" }
    } as T

    inline fun <reified T> set(key: String, value: T) = when (value) {
        is Boolean -> setBoolean(key, value)
        is Int -> setInt(key, value)
        is Float -> setFloat(key, value)
        is String -> setString(key, value)
        else -> getLogger<EditorConfig>().exitError { "Unsupported type: ${T::class.simpleName!!}" }
    }

    fun getBoolean(key: String, default: Boolean) =
        properties.getProperty(key)?.toBoolean() ?: default

    fun setBoolean(key: String, value: Boolean) {
        properties.setProperty(key, value.toString())
    }

    fun getInt(key: String, default: Int) =
        properties.getProperty(key)?.toIntOrNull() ?: default

    fun setInt(key: String, value: Int) {
        properties.setProperty(key, value.toString())
    }

    fun getFloat(key: String, default: Float) =
        properties.getProperty(key)?.toFloatOrNull() ?: default

    fun setFloat(key: String, value: Float) {
        properties.setProperty(key, value.toString())
    }

    fun getString(key: String, default: String) =
        properties.getProperty(key) ?: default

    fun setString(key: String, value: String) {
        properties.setProperty(key, value)
    }

}