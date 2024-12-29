package org.emberstudios.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.logger.exitError
import org.emberstudios.renderer.opengl.GLIndexBuffer

interface IndexBuffer {

	companion object {
		private val FACTORY_LOGGER = KotlinLogging.logger("IndexBufferFactory")

		fun create(indices: IntArray): IndexBuffer = when (Renderer.apiType) {
			RenderAPIType.OPEN_GL -> GLIndexBuffer(indices)
			RenderAPIType.VULKAN -> FACTORY_LOGGER.exitError { "Vulkan is not supported!" }
		}
	}

	val count: Int

	fun bind()
	fun unbind()
}