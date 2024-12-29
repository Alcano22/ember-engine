package org.emberstudios.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.logger.exitError
import org.emberstudios.renderer.opengl.OpenGLVertexBuffer

abstract class VertexBuffer {

	companion object {
		private val FACTORY_LOGGER = KotlinLogging.logger("VertexBufferFactory")

		fun create(): VertexBuffer = when (Renderer.apiType) {
			RenderAPIType.OPEN_GL -> OpenGLVertexBuffer()
			RenderAPIType.VULKAN -> FACTORY_LOGGER.exitError { "Vulkan is not supported!" }
		}
	}

	var layout = BufferLayout()

	abstract fun setVertices(vertices: FloatArray)

	abstract fun bind()
	abstract fun unbind()
	abstract fun delete()
}