package org.emberstudios.editor.renderer

import imgui.ImGui
import org.emberstudios.core.WindowHandle
import org.emberstudios.core.renderer.GraphicsAPIType
import org.emberstudios.core.window.WindowAPIType

/**
 * The ImGuiRenderer class is responsible for setting up and managing the ImGui context and rendering.
 *
 * @param windowAPIType The window API type to use.
 * @param graphicsAPIType The graphics API type to use.
 * @param windowHandle The window handle to use.
 * @param getCurrentContextFunc The function to get the current window context.
 * @param makeContextCurrentFunc The function to make the context current.
 */
class ImGuiRenderer(
	windowAPIType: WindowAPIType,
	graphicsAPIType: GraphicsAPIType,
	windowHandle: Long,
	getCurrentContextFunc: () -> WindowHandle,
	makeContextCurrentFunc: (WindowHandle) -> Unit
) {

	private val windowContext = ImGuiWindowContext.create(
		windowAPIType,
		windowHandle,
		getCurrentContextFunc,
		makeContextCurrentFunc
	)
	private val graphicsContext = ImGuiGraphicsContext.create(graphicsAPIType)

	/**
	 * Sets up the ImGui context.
	 */
	fun setup() {
		windowContext.setup()
		graphicsContext.setup()
	}

	/**
	 * Updates the ImGui context.
	 *
	 * @param deltaTime The time since the last frame.
	 */
	fun update(deltaTime: Float) {
		windowContext.update(deltaTime)
		graphicsContext.update(deltaTime)
	}

	/**
	 * Begins the frame.
	 */
	fun beginFrame() {
		graphicsContext.beginFrame()
		windowContext.beginFrame()
		ImGui.newFrame()
	}

	/**
	 * Ends the frame.
	 */
	fun endFrame() {
		ImGui.render()
		graphicsContext.endFrame()
		windowContext.endFrame()
	}

	/**
	 * Cleans up the ImGui context.
	 */
	fun cleanup() {
		windowContext.cleanup()
		graphicsContext.cleanup()
	}

}