package org.emberstudios.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.logger.exitError
import org.emberstudios.renderer.opengl.GLVertexArray

interface VertexArray {

	companion object {
		private val FACTORY_LOGGER = KotlinLogging.logger("VertexArrayFactory")

		fun create(): VertexArray = when (Renderer.apiType) {
			RenderAPIType.OPEN_GL -> GLVertexArray()
			RenderAPIType.VULKAN -> FACTORY_LOGGER.exitError { "Vulkan is not supported!" }
		}
	}

	fun addVertexBuffer(vertexBuffer: VertexBuffer)

	fun getIndexBuffer(): IndexBuffer?
	fun setIndexBuffer(indexBuffer: IndexBuffer)

	fun bind()
	fun unbind()
}