package org.emberstudios.engine.layer

import imgui.ImFontConfig
import imgui.ImGui
import imgui.extension.implot.ImPlot
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiConfigFlags
import imgui.flag.ImGuiDir
import org.emberstudios.core.io.ResourceManager
import org.emberstudios.core.logger.getLogger
import org.emberstudios.editor.renderer.ImGuiRenderer
import org.emberstudios.engine.Engine
import org.emberstudios.renderer.RenderContext
import org.emberstudios.renderer.Renderer
import org.emberstudios.window.Window

class ImGuiLayer(
	private val window: Window,
	renderContext: RenderContext
) : Layer {

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
		ImPlot.createContext()

		val io = ImGui.getIO()

		loadStyle()

		io.configFlags = io.configFlags or
				ImGuiConfigFlags.NavEnableKeyboard or
				ImGuiConfigFlags.DockingEnable or
				ImGuiConfigFlags.ViewportsEnable or
				ImGuiConfigFlags.DpiEnableScaleFonts

		io.fonts.clear()

		io.fontGlobalScale = window.dpiScale

		val fontConfig = ImFontConfig().apply {
			fontDataOwnedByAtlas = false
			oversampleH = 2
			oversampleV = 2
		}

		val fontData = ResourceManager.loadBinaryFile("fonts/IBMPlexSans-Regular.ttf")
		io.fonts.addFontFromMemoryTTF(fontData, 18f, fontConfig)

		ImGui.styleColorsDark()

		val style = ImGui.getStyle()
		if (io.configFlags and ImGuiConfigFlags.ViewportsEnable != 0) {
			style.windowRounding = 0f
			style.colors[ImGuiCol.WindowBg].w = 1f
		}

		renderer.setup()
	}

	private fun loadStyle() {
		val style = ImGui.getStyle()

		style.alpha = 1.0f
		style.disabledAlpha = 0.6f
		style.setWindowPadding(8.0f, 8.0f)
		style.windowRounding = 10.0f
		style.windowBorderSize = 1.0f
		style.setWindowMinSize(32f, 32f)
		style.setWindowTitleAlign(0.0f, 0.5f)
		style.windowMenuButtonPosition = ImGuiDir.Left
		style.childRounding = 10.0f
		style.childBorderSize = 1.0f
		style.popupRounding = 10.0f
		style.popupBorderSize = 1.0f
		style.setFramePadding(4.0f, 3.0f)
		style.frameRounding = 5.0f
		style.frameBorderSize = 0.0f
		style.setItemSpacing(8.0f, 4.0f)
		style.setItemInnerSpacing(4.0f, 4.0f)
		style.setCellPadding(4.0f, 2.0f)
		style.indentSpacing = 21.0f
		style.columnsMinSpacing = 6.0f
		style.scrollbarSize = 14.0f
		style.scrollbarRounding = 5.0f
		style.grabMinSize = 10.0f
		style.grabRounding = 5.0f
		style.tabRounding = 10.0f
		style.tabBorderSize = 0.0f
		style.tabMinWidthForCloseButton = 0.0f
		style.colorButtonPosition = ImGuiDir.Right
		style.setButtonTextAlign(0.5f, 0.5f)
		style.setSelectableTextAlign(0.0f, 0.0f)

		style.setColor(ImGuiCol.Text, 1.0f, 1.0f, 1.0f, 1.0f)
		style.setColor(ImGuiCol.TextDisabled, 0.6f, 0.6f, 0.6f, 1.0f)
		style.setColor(ImGuiCol.WindowBg, 0.15f, 0.15f, 0.15f, 1.0f)
		style.setColor(ImGuiCol.ChildBg, 0.15f, 0.15f, 0.15f, 1.0f)
		style.setColor(ImGuiCol.PopupBg, 0.15f, 0.15f, 0.15f, 1.0f)
		style.setColor(ImGuiCol.Border, 0.3f, 0.3f, 0.3f, 1.0f)
		style.setColor(ImGuiCol.BorderShadow, 0.3f, 0.3f, 0.3f, 1.0f)
		style.setColor(ImGuiCol.FrameBg, 0.2f, 0.2f, 0.2f, 1.0f)
		style.setColor(ImGuiCol.FrameBgHovered, 0.1f, 0.6f, 0.9f, 1.0f)
		style.setColor(ImGuiCol.FrameBgActive, 0.0f, 0.5f, 0.8f, 1.0f)
		style.setColor(ImGuiCol.TitleBg, 0.15f, 0.15f, 0.15f, 1.0f)
		style.setColor(ImGuiCol.TitleBgActive, 0.15f, 0.15f, 0.15f, 1.0f)
		style.setColor(ImGuiCol.TitleBgCollapsed, 0.15f, 0.15f, 0.15f, 1.0f)
		style.setColor(ImGuiCol.MenuBarBg, 0.2f, 0.2f, 0.2f, 1.0f)
		style.setColor(ImGuiCol.ScrollbarBg, 0.2f, 0.2f, 0.2f, 1.0f)
		style.setColor(ImGuiCol.ScrollbarGrab, 0.3f, 0.3f, 0.3f, 1.0f)
		style.setColor(ImGuiCol.ScrollbarGrabHovered, 0.35f, 0.35f, 0.35f, 1.0f)
		style.setColor(ImGuiCol.ScrollbarGrabActive, 0.35f, 0.35f, 0.35f, 1.0f)
		style.setColor(ImGuiCol.CheckMark, 0.0f, 0.5f, 0.8f, 1.0f)
		style.setColor(ImGuiCol.SliderGrab, 0.1f, 0.6f, 0.9f, 1.0f)
		style.setColor(ImGuiCol.SliderGrabActive, 0.0f, 0.45f, 0.8f, 1.0f)
		style.setColor(ImGuiCol.Button, 0.2f, 0.2f, 0.2f, 1.0f)
		style.setColor(ImGuiCol.ButtonHovered, 0.1f, 0.6f, 0.9f, 1.0f)
		style.setColor(ImGuiCol.ButtonActive, 0.1f, 0.6f, 0.9f, 1.0f)
		style.setColor(ImGuiCol.Header, 0.2f, 0.2f, 0.2f, 1.0f)
		style.setColor(ImGuiCol.HeaderHovered, 0.1f, 0.6f, 0.9f, 1.0f)
		style.setColor(ImGuiCol.HeaderActive, 0.0f, 0.45f, 0.8f, 1.0f)
		style.setColor(ImGuiCol.Separator, 0.3f, 0.3f, 0.3f, 1.0f)
		style.setColor(ImGuiCol.SeparatorHovered, 0.3f, 0.3f, 0.3f, 1.0f)
		style.setColor(ImGuiCol.SeparatorActive, 0.3f, 0.3f, 0.3f, 1.0f)
		style.setColor(ImGuiCol.ResizeGrip, 0.15f, 0.15f, 0.15f, 1.0f)
		style.setColor(ImGuiCol.ResizeGripHovered, 0.2f, 0.2f, 0.2f, 1.0f)
		style.setColor(ImGuiCol.ResizeGripActive, 0.3f, 0.3f, 0.3f, 1.0f)
		style.setColor(ImGuiCol.Tab, 0.15f, 0.15f, 0.15f, 1.0f)
		style.setColor(ImGuiCol.TabHovered, 0.1f, 0.6f, 0.9f, 1.0f)
		style.setColor(ImGuiCol.TabActive, 0.0f, 0.45f, 0.8f, 1.0f)
		style.setColor(ImGuiCol.TabUnfocused, 0.15f, 0.15f, 0.15f, 1.0f)
		style.setColor(ImGuiCol.TabUnfocusedActive, 0.0f, 0.45f, 0.8f, 1.0f)
		style.setColor(ImGuiCol.PlotLines, 0.0f, 0.45f, 0.8f, 1.0f)
		style.setColor(ImGuiCol.PlotLinesHovered, 0.1f, 0.6f, 0.9f, 1.0f)
		style.setColor(ImGuiCol.PlotHistogram, 0.0f, 0.45f, 0.8f, 1.0f)
		style.setColor(ImGuiCol.PlotHistogramHovered, 0.1f, 0.6f, 0.9f, 1.0f)
		style.setColor(ImGuiCol.TableHeaderBg, 0.2f, 0.2f, 0.2f, 1.0f)
		style.setColor(ImGuiCol.TableBorderStrong, 0.3f, 0.3f, 0.35f, 1.0f)
		style.setColor(ImGuiCol.TableBorderLight, 0.2f, 0.2f, 0.25f, 1.0f)
		style.setColor(ImGuiCol.TableRowBg, 0.0f, 0.0f, 0.0f, 0.0f)
		style.setColor(ImGuiCol.TableRowBgAlt, 1.0f, 1.0f, 1.0f, 0.05f)
		style.setColor(ImGuiCol.TextSelectedBg, 0.0f, 0.45f, 0.8f, 1.0f)
		style.setColor(ImGuiCol.DragDropTarget, 0.15f, 0.15f, 0.15f, 1.0f)
		style.setColor(ImGuiCol.NavHighlight, 0.15f, 0.15f, 0.15f, 1.0f)
		style.setColor(ImGuiCol.NavWindowingHighlight, 1.0f, 1.0f, 1.0f, 0.7f)
		style.setColor(ImGuiCol.NavWindowingDimBg, 0.8f, 0.8f, 0.8f, 0.2f)
		style.setColor(ImGuiCol.ModalWindowDimBg, 0.15f, 0.15f, 0.15f, 1.0f)
	}

	override fun onDetach() {
		renderer.cleanup()
	}

	override fun onUpdate(deltaTime: Float) = renderer.update(deltaTime)

	fun begin() = renderer.beginFrame()
	fun end() = renderer.endFrame()

}