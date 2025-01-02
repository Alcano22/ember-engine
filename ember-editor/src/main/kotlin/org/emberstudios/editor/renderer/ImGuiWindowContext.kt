package org.emberstudios.editor.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.WindowHandle
import org.emberstudios.core.window.WindowAPIType
import org.emberstudios.editor.renderer.impl.ImGuiGLFWContext

interface ImGuiWindowContext {

	companion object {
		private val LOGGER = KotlinLogging.logger("ImGuiGraphicsContextFactory")

		fun create(
			apiType: WindowAPIType,
			windowHandle: WindowHandle,
			getCurrentContextFunc: () -> WindowHandle,
			makeContextCurrentFunc: (WindowHandle) -> Unit
		): ImGuiWindowContext = when (apiType) {
			WindowAPIType.GLFW -> ImGuiGLFWContext(windowHandle, getCurrentContextFunc, makeContextCurrentFunc)
		}
	}

	fun setup()
	fun update(deltaTime: Float)

	fun beginFrame()
	fun endFrame()

	fun cleanup()

}