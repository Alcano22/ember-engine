package org.emberstudios.window

import org.emberstudios.core.window.WindowAPIType
import org.emberstudios.renderer.RenderContext
import org.emberstudios.window.glfw.GLFWWindow
import org.joml.Vector2i

/**
 * Interface for a window.
 */
interface Window {

	companion object {
		/**
		 * API type of the window.
		 */
		lateinit var apiType: WindowAPIType

		/**
		 * Create a window.
		 */
		fun create(apiType: WindowAPIType): Window {
			this.apiType = apiType
			return when (apiType) {
				WindowAPIType.GLFW -> GLFWWindow()
			}
		}
	}

	/**
	 * Size of the window in pixels.
	 */
	val size: Vector2i

	/**
	 * Whether the window should close next frame.
	 */
	val shouldClose: Boolean

	/**
	 * Native handle of the window.
	 */
	val nativeHandle: Long

	/**
	 * DPI scale of the window.
	 */
	val dpiScale: Float

	/**
	 * Time since the window was opened in seconds.
	 */
	val time: Float

	/**
	 * Whether the cursor is locked.
	 */
	var lockedCursor: Boolean

	/**
	 * Initialize the window.
	 *
	 * @param title Title of the window.
	 * @param width Width of the window in pixels.
	 * @param height Height of the window in pixels.
	 */
	fun init(title: String, width: Int, height: Int): Boolean

	/**
	 * Update the window.
	 */
	fun update()

	/**
	 * Destroy the window.
	 */
	fun destroy()

	/**
	 * Quit the window.
	 */
	fun quit()

	/**
	 * Set the resize callback.
	 */
	fun setResizeCallback(callback: (Int, Int) -> Unit)

	/**
	 * Create a render context.
	 */
	fun createRenderContext(): RenderContext

}