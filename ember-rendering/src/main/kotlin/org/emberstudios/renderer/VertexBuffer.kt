package org.emberstudios.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.logger.exitError
import org.emberstudios.core.renderer.GraphicsAPIType
import org.emberstudios.renderer.opengl.GLVertexBuffer

abstract class VertexBuffer {

	companion object {
		private val FACTORY_LOGGER = KotlinLogging.logger("VertexBufferFactory")

		fun create(vertices: FloatArray): VertexBuffer = when (Renderer.apiType) {
			GraphicsAPIType.OPEN_GL -> GLVertexBuffer(vertices)
			GraphicsAPIType.VULKAN -> FACTORY_LOGGER.exitError { "Vulkan is not supported!" }
		}
	}

	var layout = BufferLayout()

	abstract fun bind()
	abstract fun unbind()
	abstract fun delete()
}