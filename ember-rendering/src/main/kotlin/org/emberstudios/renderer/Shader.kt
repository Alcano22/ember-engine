package org.emberstudios.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.io.Resource
import org.emberstudios.core.io.ResourceManager
import org.emberstudios.core.logger.exitError
import org.emberstudios.core.logger.getLogger
import org.emberstudios.core.renderer.GraphicsAPIType
import org.emberstudios.renderer.opengl.GLShader

interface Shader : Resource {
	companion object {
		private val FACTORY_LOGGER = KotlinLogging.logger("ShaderFactory")

		fun create(vertexSrc: String, fragmentSrc: String): Shader = when (Renderer.apiType) {
			GraphicsAPIType.OPEN_GL -> GLShader(vertexSrc, fragmentSrc)
			GraphicsAPIType.VULKAN -> FACTORY_LOGGER.exitError { "Vulkan is not supported!" }
		}

		fun create(filepath: String): Shader = when (Renderer.apiType) {
			GraphicsAPIType.OPEN_GL -> GLShader(filepath)
			GraphicsAPIType.VULKAN -> FACTORY_LOGGER.exitError { "Vulkan is not supported!" }
		}
	}

	fun loadFromFile()
	fun compile()
	fun reload()

	fun <T> setUniform(name: String, value: T)

	fun bind()
	fun unbind()
}

fun ResourceManager.loadShader(filepath: String): Shader {
	val (shader, cached) = load(filepath) { Shader.create(it) }
	if (!cached)
		getLogger<ResourceManager>().trace { "Loaded shader: '$filepath'" }

	ShaderLibrary.registerShader(filepath, shader)
	return shader
}