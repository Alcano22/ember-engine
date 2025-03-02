package org.emberstudios.engine.editor

import imgui.ImGui
import imgui.ImVec2
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiWindowFlags
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.layer.EditorLayer
import org.emberstudios.engine.layer.EditorLayer.RuntimeState
import org.emberstudios.engine.networking.NetworkingManager
import org.emberstudios.engine.util.Time
import org.emberstudios.input.Input
import org.emberstudios.renderer.Camera
import org.emberstudios.renderer.Framebuffer
import org.emberstudios.renderer.Renderer
import org.joml.Vector2f
import kotlin.math.round

class ViewportWindow(
	private val framebuffer: Framebuffer,
	private val editorLayer: EditorLayer
) : EditorWindow("Viewport") {

	companion object {
		const val MENU_BAR_PADDING = 25f
		const val STATS_WINDOW_WIDTH = 200f
		const val STATS_WINDOW_HEIGHT = 80f
		const val STATS_WINDOW_PADDING = 20f

		val LOGGER = getLogger<ViewportWindow>()
	}

	private var runtimeState
		get() = editorLayer.runtimeState
		set(value) { editorLayer.runtimeState = value }

	private var showStats = false
	private var fps = 0
	private var nextUpdateFPSTime = 0f

	override fun update(deltaTime: Float) {
		if (nextUpdateFPSTime <= Time.time) {
			nextUpdateFPSTime = Time.time + 1f
			fps = round(1f / deltaTime).toInt()
		}
	}

	override fun renderMenuBar() {
		val label = when (runtimeState) {
			RuntimeState.EDITOR -> "Start"
			RuntimeState.PLAYING -> "Stop"
			RuntimeState.PAUSED -> "Stop"
		}

		ImGui.setCursorPosX(ImGui.getCursorPosX() + MENU_BAR_PADDING)

		if (ImGui.menuItem(label, runtimeState == RuntimeState.PLAYING || runtimeState == RuntimeState.PAUSED)) {
			runtimeState = when (runtimeState) {
				RuntimeState.EDITOR -> RuntimeState.PLAYING
				RuntimeState.PLAYING -> RuntimeState.EDITOR
				RuntimeState.PAUSED -> RuntimeState.EDITOR
			}

			if (runtimeState == RuntimeState.EDITOR) editorLayer.reloadScene()
			if (runtimeState == RuntimeState.PLAYING) editorLayer.saveScene()
		}

		if (ImGui.menuItem("Pause", runtimeState == RuntimeState.PAUSED, runtimeState != RuntimeState.EDITOR)) {
			if (runtimeState == RuntimeState.PLAYING)
				runtimeState = RuntimeState.PAUSED
			else if (runtimeState == RuntimeState.PAUSED)
				runtimeState = RuntimeState.PLAYING
		}

		val statsLabel = "Stats"
		val statsWidth = ImGui.calcTextSizeX(statsLabel) + ImGui.getStyle().itemSpacingX * 2f

		ImGui.sameLine(ImGui.getWindowSizeX() - statsWidth - MENU_BAR_PADDING)

		if (ImGui.menuItem(statsLabel))
			showStats = !showStats
	}

	override fun renderContent() {
		val viewportFocused = ImGui.isWindowFocused()
		Input.blockingCallback = {
			val io = ImGui.getIO()

			val blockKeyboard = io.wantCaptureKeyboard && !viewportFocused
			val blockMouse = io.wantCaptureMouse && !viewportFocused

			Pair(blockKeyboard, blockMouse)
		}

		val viewportSize = getViewportLargestSize()
		Camera.viewportSize = Vector2f(viewportSize.x, viewportSize.y)
		if (framebuffer.width != viewportSize.x.toInt() || framebuffer.height != viewportSize.y.toInt()) {
			framebuffer.resize(viewportSize.x.toInt(), viewportSize.y.toInt())
			Renderer.viewport(viewportSize.x.toInt(), viewportSize.y.toInt())
		}

		val viewportPos = getViewportCenteredPosition(viewportSize)

		ImGui.setCursorPos(viewportPos)
		val mousePos = ImGui.getMousePos()
		val viewportScreenPos = ImGui.getCursorScreenPos()
		val relativeMousePos = Vector2f(mousePos.x - viewportScreenPos.x, mousePos.y - viewportScreenPos.y)
		val relativeMousePosValid =
				relativeMousePos.x >= 0f &&
				relativeMousePos.y >= 0f &&
				relativeMousePos.x <= viewportSize.x &&
				relativeMousePos.y <= viewportSize.y
		Input.mouseViewportPosition = if (relativeMousePosValid) relativeMousePos else Vector2f(Float.NaN, Float.NaN)

		val textureID = framebuffer.colorAttachmentID.toLong()
		ImGui.image(textureID, viewportSize, ImVec2(0f, 1f), ImVec2(1f, 0f))

		if (showStats) {
			ImGui.setCursorPos(
				viewportPos.x + viewportSize.x - STATS_WINDOW_WIDTH - STATS_WINDOW_PADDING,
				viewportPos.y + STATS_WINDOW_PADDING
			)

			ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.1f, 0.1f, 0.1f, 0.4f)

			if (ImGui.beginChild(
				"Stats",
				STATS_WINDOW_WIDTH,
				STATS_WINDOW_HEIGHT,
				true,
				ImGuiWindowFlags.NoResize or
						ImGuiWindowFlags.AlwaysAutoResize or
						ImGuiWindowFlags.NoCollapse
			)) {
				ImGui.text("Stats")
				ImGui.separator()
				ImGui.text("FPS: $fps")

				ImGui.endChild()

				ImGui.popStyleColor()
			}
		}
	}

	private fun getViewportLargestSize(): ImVec2 {
		val windowSize = ImGui.getContentRegionAvail()
		windowSize.x -= ImGui.getScrollX()
		windowSize.y -= ImGui.getScrollY()

		val aspectRatio = 16f / 9f

		var aspectWidth = windowSize.x
		var aspectHeight = aspectWidth / aspectRatio
		if (aspectHeight > windowSize.y) {
			aspectHeight = windowSize.y
			aspectWidth = aspectHeight * aspectRatio
		}

		return ImVec2(aspectWidth, aspectHeight)
	}

	private fun getViewportCenteredPosition(aspectSize: ImVec2): ImVec2 {
		val windowSize = ImGui.getContentRegionAvail()
		windowSize.x -= ImGui.getScrollX()
		windowSize.y -= ImGui.getScrollY()

		val viewportX = (windowSize.x - aspectSize.x) / 2f
		val viewportY = (windowSize.y - aspectSize.y) / 2f

		return ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY())
	}
}