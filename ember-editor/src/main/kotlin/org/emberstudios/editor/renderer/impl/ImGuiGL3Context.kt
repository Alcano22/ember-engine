package org.emberstudios.editor.renderer.impl

import imgui.ImGui
import imgui.gl3.ImGuiImplGl3
import org.emberstudios.editor.renderer.ImGuiGraphicsContext

internal class ImGuiGL3Context : ImGuiGraphicsContext {

	private lateinit var impl: ImGuiImplGl3

	override fun setup() {
		impl = ImGuiImplGl3()
		impl.init("#version 460")
	}

	override fun update(deltaTime: Float) {}

	override fun beginFrame() = impl.newFrame()
	override fun endFrame() = impl.renderDrawData(ImGui.getDrawData())

	override fun cleanup() = impl.shutdown()

}