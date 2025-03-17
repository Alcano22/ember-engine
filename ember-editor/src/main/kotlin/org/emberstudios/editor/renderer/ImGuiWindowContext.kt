package org.emberstudios.editor.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.WindowHandle
import org.emberstudios.core.window.WindowAPIType
import org.emberstudios.editor.renderer.impl.ImGuiGLFWContext

/**
 * Interface for the ImGui window context.
 */
interface ImGuiWindowContext {

	companion object {
		private val LOGGER = KotlinLogging.logger("ImGuiGraphicsContextFactory")

		/**
		 * Create a new ImGui window context.
		 *
		 * @param apiType The window API type.
		 * @param windowHandle The window handle.
		 * @param getCurrentContextFunc The function to get the current context.
		 * @param makeContextCurrentFunc The function to make the context current.
		 *
		 * @return The new ImGui window context.
		 */
		fun create(
			apiType: WindowAPIType,
			windowHandle: WindowHandle,
			getCurrentContextFunc: () -> WindowHandle,
			makeContextCurrentFunc: (WindowHandle) -> Unit
		): ImGuiWindowContext = when (apiType) {
			WindowAPIType.GLFW -> ImGuiGLFWContext(windowHandle, getCurrentContextFunc, makeContextCurrentFunc)
		}
	}

	/**
	 * Setup the ImGui window context.
	 */
	fun setup()

	/**
	 * Update the ImGui window context.
	 *
	 * @param deltaTime The delta time.
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
	 * Cleanup the ImGui window context.
	 */
	fun cleanup()

}