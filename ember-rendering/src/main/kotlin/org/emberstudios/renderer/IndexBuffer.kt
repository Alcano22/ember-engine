package org.emberstudios.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.logger.exitError
import org.emberstudios.core.renderer.GraphicsAPIType
import org.emberstudios.renderer.opengl.GLIndexBuffer

interface IndexBuffer {

	companion object {
		private val FACTORY_LOGGER = KotlinLogging.logger("IndexBufferFactory")

		/**
		 * Create an index buffer.
		 *
		 * @param indices The indices to store in the buffer.
		 *
		 * @return The created index buffer.
		 */
		fun create(indices: IntArray): IndexBuffer = when (Renderer.apiType) {
			GraphicsAPIType.OPEN_GL -> GLIndexBuffer(indices)
			GraphicsAPIType.VULKAN -> FACTORY_LOGGER.exitError { "Vulkan is not supported!" }
		}
	}

	val count: Int

	fun bind()
	fun unbind()
	fun delete()
}