package org.emberstudios.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.io.Resource
import org.emberstudios.core.io.ResourceManager
import org.emberstudios.core.logger.exitError
import org.emberstudios.renderer.opengl.GLShader

interface Shader : Resource {
	companion object {
		private val FACTORY_LOGGER = KotlinLogging.logger("ShaderFactory")

		fun create(vertexSrc: String, fragmentSrc: String): Shader = when (Renderer.apiType) {
			RenderAPIType.OPEN_GL -> GLShader(vertexSrc, fragmentSrc)
			RenderAPIType.VULKAN -> FACTORY_LOGGER.exitError { "Vulkan is not supported!" }
		}

		fun create(filepath: String): Shader = when (Renderer.apiType) {
			RenderAPIType.OPEN_GL -> GLShader(filepath)
			RenderAPIType.VULKAN -> FACTORY_LOGGER.exitError { "Vulkan is not supported!" }
		}
	}

	fun compile()
	fun <T> setUniform(name: String, value: T)

	fun bind()
	fun unbind()
}

fun ResourceManager.loadShader(path: String) = load(path) { Shader.create(it) }