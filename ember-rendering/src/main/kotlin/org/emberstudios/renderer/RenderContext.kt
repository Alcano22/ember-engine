package org.emberstudios.renderer

import org.emberstudios.core.WindowHandle

/**
 * Interface for the render context.
 */
interface RenderContext {

	/**
	 * Initialize the render context.
	 */
	fun init()

	/**
	 * Initialize the logging system.
	 */
	fun initLog(logDir: String)

	/**
	 * Swap the buffers of the render context.
	 */
	fun swapBuffers()

	/**
	 * Make the given [windowHandle] the current context.
	 *
	 * @param windowHandle The window handle to make the current context.
	 */
	fun makeContextCurrent(windowHandle: WindowHandle)

	/**
	 * Get the current context.
	 */
	fun getCurrentContext(): WindowHandle

}