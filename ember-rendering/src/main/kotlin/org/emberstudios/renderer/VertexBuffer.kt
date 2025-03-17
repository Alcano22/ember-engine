package org.emberstudios.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.logger.exitError
import org.emberstudios.core.renderer.GraphicsAPIType
import org.emberstudios.renderer.opengl.GLVertexBuffer

/**
 * VertexBuffer is an abstract class that represents a buffer of vertices.
 */
abstract class VertexBuffer {

	companion object {
		private val FACTORY_LOGGER = KotlinLogging.logger("VertexBufferFactory")

		/**
		 * Create a new VertexBuffer.
		 *
		 * @param vertices The vertices to create the buffer with.
		 *
		 * @return The created VertexBuffer.
		 */
		fun create(vertices: FloatArray): VertexBuffer = when (Renderer.apiType) {
			GraphicsAPIType.OPEN_GL -> GLVertexBuffer(vertices)
			GraphicsAPIType.VULKAN -> FACTORY_LOGGER.exitError { "Vulkan is not supported!" }
		}
	}

	/**
	 * The layout of the buffer.
	 */
	var layout = BufferLayout()

	/**
	 * Bind the buffer.
	 */
	abstract fun bind()

	/**
	 * Unbind the buffer.
	 */
	abstract fun unbind()

	/**
	 * Delete the buffer.
	 */
	abstract fun delete()
}