package org.emberstudios.editor.renderer.impl

import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import imgui.glfw.ImGuiImplGlfw
import org.emberstudios.core.WindowHandle
import org.emberstudios.editor.renderer.ImGuiWindowContext

internal class ImGuiGLFWContext(
	private val windowHandle: Long,
	private val getCurrentContextFunc: () -> WindowHandle,
	private val makeContextCurrentFunc: (WindowHandle) -> Unit
) : ImGuiWindowContext {

	private lateinit var impl: ImGuiImplGlfw

	override fun setup() {
		impl = ImGuiImplGlfw()
		impl.init(windowHandle, true)
	}

	override fun update(deltaTime: Float) {}

	override fun beginFrame() = impl.newFrame()

	override fun endFrame() {
		if (ImGui.getIO().configFlags and ImGuiConfigFlags.ViewportsEnable != 0) {
			val backupCurrentContext = getCurrentContextFunc()
			ImGui.updatePlatformWindows()
			ImGui.renderPlatformWindowsDefault()
			makeContextCurrentFunc(backupCurrentContext)
		}
	}

	override fun cleanup() = impl.shutdown()

}