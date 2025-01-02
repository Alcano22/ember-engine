package org.emberstudios.engine.layer

import imgui.ImGui
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiConfigFlags
import org.emberstudios.core.logger.getLogger
import org.emberstudios.editor.renderer.ImGuiRenderer
import org.emberstudios.engine.Engine
import org.emberstudios.renderer.RenderContext
import org.emberstudios.renderer.Renderer
import org.emberstudios.window.Window
import org.emberstudios.window.glfw.GLFWContext

class ImGuiLayer(renderContext: RenderContext) : Layer {

	companion object {
		private val LOGGER = getLogger<ImGuiLayer>()
	}

	private val renderer = ImGuiRenderer(
		Window.apiType,
		Renderer.apiType,
		Engine.window.nativeHandle,
		{ renderContext.getCurrentContext() },
		{ renderContext.makeContextCurrent(it) }
	)

	override fun onAttach() {
		ImGui.createContext()

		val io = ImGui.getIO()
		io.configFlags = io.configFlags or
				ImGuiConfigFlags.NavEnableKeyboard or
				ImGuiConfigFlags.DockingEnable or
				ImGuiConfigFlags.ViewportsEnable or
				ImGuiConfigFlags.DpiEnableScaleFonts

		ImGui.styleColorsDark()

		val style = ImGui.getStyle()
		if (io.configFlags and ImGuiConfigFlags.ViewportsEnable != 0) {
			style.windowRounding = 0f
			style.colors[ImGuiCol.WindowBg].w = 1f
		}

		renderer.setup()
	}

	override fun onDetach() = renderer.cleanup()

	override fun onUpdate(deltaTime: Float) = renderer.update(deltaTime)

	fun begin() = renderer.beginFrame()
	fun end() = renderer.endFrame()

}