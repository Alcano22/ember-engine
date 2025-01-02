package org.emberstudios.editor.renderer

import imgui.ImGui
import org.emberstudios.core.WindowHandle
import org.emberstudios.core.renderer.GraphicsAPIType
import org.emberstudios.core.window.WindowAPIType

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

	fun setup() {
		windowContext.setup()
		graphicsContext.setup()
	}

	fun update(deltaTime: Float) {
		windowContext.update(deltaTime)
		graphicsContext.update(deltaTime)
	}

	fun beginFrame() {
		graphicsContext.beginFrame()
		windowContext.beginFrame()
		ImGui.newFrame()
	}

	fun endFrame() {
		ImGui.render()
		graphicsContext.endFrame()
		windowContext.endFrame()
	}

	fun cleanup() {
		windowContext.cleanup()
		graphicsContext.cleanup()
	}

}