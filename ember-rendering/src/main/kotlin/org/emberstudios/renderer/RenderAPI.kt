package org.emberstudios.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.logger.exitError
import org.emberstudios.core.renderer.GraphicsAPIType
import org.emberstudios.renderer.opengl.GLRenderAPI

/**
 * Interface for the rendering API.
 */
interface RenderAPI {

	companion object {
		private val LOGGER = KotlinLogging.logger("RenderAPIFactory")

		/**
		 * Create a new rendering API based on the given [apiType].
		 *
		 * @param apiType The type of rendering API to create.
		 *
		 * @return The created rendering API.
		 */
		fun create(apiType: GraphicsAPIType): RenderAPI = when (apiType) {
			GraphicsAPIType.OPEN_GL -> GLRenderAPI()
			GraphicsAPIType.VULKAN -> LOGGER.exitError { "Vulkan is not supported!" }
		}
	}

	/**
	 * Initialize the rendering API.
	 */
	fun init()

	/**
	 * Initialize the logging system.
	 *
	 * @param logDir The directory to store the logs in.
	 */
	fun initLog(logDir: String)

	/**
	 * Set the clear color of the rendering API.
	 *
	 * @param r The red component of the color.
	 * @param g The green component of the color.
	 * @param b The blue component of the color.
	 * @param a The alpha component of the color.
	 */
	fun clear(r: Float, g: Float, b: Float, a: Float)

	/**
	 * Set the viewport of the rendering API.
	 *
	 * @param width The width of the viewport.
	 * @param height The height of the viewport.
	 */
	fun viewport(width: Int, height: Int)

	/**
	 * Draw the given [vertexArray] using the rendering API.
	 *
	 * @param vertexArray The vertex array to draw.
	 */
	fun drawIndexed(vertexArray: VertexArray)
}