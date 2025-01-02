package org.emberstudios.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.logger.exitError
import org.emberstudios.core.renderer.GraphicsAPIType
import org.emberstudios.renderer.opengl.GLRenderAPI

interface RenderAPI {

	companion object {
		private val LOGGER = KotlinLogging.logger("RenderAPIFactory")

		fun create(apiType: GraphicsAPIType): RenderAPI = when (apiType) {
			GraphicsAPIType.OPEN_GL -> GLRenderAPI()
			GraphicsAPIType.VULKAN -> LOGGER.exitError { "Vulkan is not supported!" }
		}
	}

	fun init()
	fun initLog(logDir: String)

	fun clear(r: Float, g: Float, b: Float, a: Float)
	fun viewport(width: Int, height: Int)

	fun drawIndexed(vertexArray: VertexArray)
}