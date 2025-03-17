package org.emberstudios.engine.editor

import imgui.ImGui
import imgui.ImVec2
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiWindowFlags
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.Engine
import org.emberstudios.engine.layer.EditorLayer
import org.emberstudios.engine.runtime.GameRuntime
import org.emberstudios.engine.util.Time
import org.emberstudios.input.Input
import org.emberstudios.renderer.Camera
import org.emberstudios.renderer.Framebuffer
import org.emberstudios.renderer.Renderer
import org.joml.Vector2f
import kotlin.math.max
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

	private val gameRuntime get() = Engine.gameRuntime

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
		val label = when (gameRuntime.state) {
			GameRuntime.State.STOPPED -> "Start"
			GameRuntime.State.PLAYING -> "Stop"
			GameRuntime.State.PAUSED -> "Stop"
		}

		ImGui.setCursorPosX(ImGui.getCursorPosX() + MENU_BAR_PADDING)

		if (ImGui.menuItem(label, gameRuntime.isPlayingOrPaused))
			gameRuntime.togglePlay()

		if (ImGui.menuItem("Pause", gameRuntime.isPaused, gameRuntime.isPlayingOrPaused))
			gameRuntime.togglePause()

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
		val contentAvail = ImGui.getContentRegionAvail()
		val scrollX = ImGui.getScrollX()
		val scrollY = ImGui.getScrollY()
		val availX = max(contentAvail.x - scrollX, 1f)
		val availY = max(contentAvail.y - scrollY, 1f)
		val aspectRatio = 16f / 9f
		var aspectWidth = availX
		var aspectHeight = aspectWidth / aspectRatio
		if (aspectHeight > availY) {
			aspectHeight = availY
			aspectWidth = aspectHeight * aspectRatio
		}
		return ImVec2(aspectWidth, aspectHeight)
	}

	private fun getViewportCenteredPosition(aspectSize: ImVec2): ImVec2 {
		val contentAvail = ImGui.getContentRegionAvail()
		val scrollX = ImGui.getScrollX()
		val scrollY = ImGui.getScrollY()
		val availX = max(contentAvail.x - scrollX, 1f)
		val availY = max(contentAvail.y - scrollY, 1f)
		val viewportX = (availX - aspectSize.x) / 2f
		val viewportY = (availY - aspectSize.y) / 2f
		return ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY())
	}
}