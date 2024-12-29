package org.emberstudios.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.logger.exitError
import org.emberstudios.renderer.opengl.OpenGLShader

interface Shader {
	companion object {
		private val FACTORY_LOGGER = KotlinLogging.logger("ShaderFactory")

		fun create(vertexSrc: String, fragmentSrc: String): Shader = when (Renderer.apiType) {
			RenderAPIType.OPEN_GL -> OpenGLShader(vertexSrc, fragmentSrc)
			RenderAPIType.VULKAN -> FACTORY_LOGGER.exitError { "Vulkan is not supported!" }
		}
	}

	fun compile()
	fun <T> setUniform(name: String, value: T)

	fun bind()
	fun unbind()
	fun delete()
}