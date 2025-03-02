package org.emberstudios.engine.editor

import imgui.ImGui
import imgui.ImVec2
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.util.Color
import org.emberstudios.input.Input

class ControllerTestWindow : EditorWindow("Controller Test") {

	private enum class Stick { LEFT, RIGHT }

	companion object {
		val LOGGER = getLogger<ControllerTestWindow>()
	}

	override fun renderContent() {
		val spacing = 15f
		val radius = 50f

		val drawList = ImGui.getWindowDrawList()
		for ((index, stick) in Stick.entries.withIndex()) {
			val pos = ImGui.getCursorScreenPos()
			val offsetX = index * (radius * 2 + spacing)
			val centerX = pos.x + radius + offsetX
			val centerY = pos.y + radius
			drawList.addCircle(
				ImVec2(centerX, centerY),
				radius,
				Color.HEX_WHITE,
				0,
				2f
			)

			val rawLeftStick = when (stick) {
				Stick.LEFT -> Input.controllerLeftStick
				Stick.RIGHT -> Input.controllerRightStick
			}
			val leftStick = if (rawLeftStick.length() > 1f) rawLeftStick.normalize() else rawLeftStick
			val dotX = centerX + leftStick.x * radius
			val dotY = centerY - leftStick.y * radius
			drawList.addCircleFilled(
				ImVec2(dotX, dotY),
				5f,
				Color.HEX_RED
			)
		}
	}

}