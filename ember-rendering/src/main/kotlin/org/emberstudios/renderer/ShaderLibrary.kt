package org.emberstudios.renderer

import org.emberstudios.core.logger.getLogger

object ShaderLibrary {

    private val LOGGER = getLogger<ShaderLibrary>()

    private val loadedShaders = mutableMapOf<String, Shader>()

    fun registerShader(filepath: String, shader: Shader) { loadedShaders[filepath] = shader }

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