package org.emberstudios.editor.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.logger.exitError
import org.emberstudios.core.renderer.GraphicsAPIType
import org.emberstudios.editor.renderer.ImGuiWindowContext.Companion
import org.emberstudios.editor.renderer.impl.ImGuiGL3Context

interface ImGuiGraphicsContext {

	companion object {
		private val LOGGER = KotlinLogging.logger("ImGuiGraphicsContextFactory")

		fun create(apiType: GraphicsAPIType): ImGuiGraphicsContext = when (apiType) {
			GraphicsAPIType.OPEN_GL -> ImGuiGL3Context()
			GraphicsAPIType.VULKAN -> LOGGER.exitError { "Vulkan is not supported!" }
		}
	}

	fun setup()
	fun update(deltaTime: Float)

	fun beginFrame()
	fun endFrame()

	fun cleanup()

}