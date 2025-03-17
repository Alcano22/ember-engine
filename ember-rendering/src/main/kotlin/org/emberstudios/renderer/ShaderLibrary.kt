package org.emberstudios.renderer

import org.emberstudios.core.logger.getLogger

/**
 * ShaderLibrary is a singleton object that manages the loading and reloading of shaders.
 */
object ShaderLibrary {

    private val LOGGER = getLogger<ShaderLibrary>()

    private val loadedShaders = mutableMapOf<String, Shader>()

    /**
     * Load a shader from a file and register it with the library.
     *
     * @param filepath The path to the shader file.
     * @param shader The shader object to load the shader into.
     */
    fun registerShader(filepath: String, shader: Shader) { loadedShaders[filepath] = shader }

    /**
     * Get a shader from the library.
     *
     * @param filepath The path to the shader file.
     */
    fun reloadShader(filepath: String) {
        val shader = loadedShaders[filepath]
        if (shader == null) {
            LOGGER.warn { "No loaded shader found for: '$filepath'" }
            return
        }

        shader.reload()
        LOGGER.trace { "Reloaded shader: '$filepath'" }
    }

}