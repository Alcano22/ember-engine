package org.emberstudios.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.logger.exitError
import org.emberstudios.core.renderer.GraphicsAPIType
import org.emberstudios.renderer.opengl.GLVertexArray

/**
 * VertexArray interface.
 */
interface VertexArray {

	companion object {
		private val FACTORY_LOGGER = KotlinLogging.logger("VertexArrayFactory")

		/**
		 * Create a new VertexArray.
		 *
		 * @return The new VertexArray.
		 */
		fun create(): VertexArray = when (Renderer.apiType) {
			GraphicsAPIType.OPEN_GL -> GLVertexArray()
			GraphicsAPIType.VULKAN -> FACTORY_LOGGER.exitError { "Vulkan is not supported!" }
		}
	}

	/**
	 * Add a VertexBuffer to the VertexArray.
	 */
	fun addVertexBuffer(vertexBuffer: VertexBuffer)

	/**
	 * Get the IndexBuffer
	 */
	fun getIndexBuffer(): IndexBuffer?

	/**
	 * Set the IndexBuffer
	 */
	fun setIndexBuffer(indexBuffer: IndexBuffer)

	/**
	 * Bind the VertexArray.
	 */
	fun bind()

	/**
	 * Unbind the VertexArray.
	 */
	fun unbind()
}