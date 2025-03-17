package org.emberstudios.editor.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.logger.exitError
import org.emberstudios.core.renderer.GraphicsAPIType
import org.emberstudios.editor.renderer.ImGuiWindowContext.Companion
import org.emberstudios.editor.renderer.impl.ImGuiGL3Context

/**
 * Interface for the ImGui graphics context.
 */
interface ImGuiGraphicsContext {

	companion object {
		private val LOGGER = KotlinLogging.logger("ImGuiGraphicsContextFactory")

		/**
		 * Create a new ImGui graphics context.
		 *
		 * @param apiType The graphics API type.
		 *
		 * @return The ImGui graphics context.
		 */
		fun create(apiType: GraphicsAPIType): ImGuiGraphicsContext = when (apiType) {
			GraphicsAPIType.OPEN_GL -> ImGuiGL3Context()
			GraphicsAPIType.VULKAN -> LOGGER.exitError { "Vulkan is not supported!" }
		}
	}

	/**
	 * Setup the ImGui graphics context.
	 */
	fun setup()

	/**
	 * Update the ImGui graphics context.
	 */
	fun update(deltaTime: Float)

	/**
	 * Begin the ImGui frame.
	 */
	fun beginFrame()

	/**
	 * End the ImGui frame.
	 */
	fun endFrame()

	/**
	 * Cleanup the ImGui graphics context.
	 */
	fun cleanup()

}