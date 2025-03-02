package org.emberstudios.engine.editor

import imgui.ImGui
import imgui.ImVec2
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.util.Color
import org.emberstudios.core.util.approx
import org.emberstudios.core.util.coerceIn01
import org.emberstudios.input.Input

class ControllerTestWindow : EditorWindow("Controller Test") {

	private enum class ControllerSide { LEFT, RIGHT }

	companion object {
		val LOGGER = getLogger<ControllerTestWindow>()
	}

	override fun renderContent() {
		val spacing = 15f

		val pos = ImGui.getCursorScreenPos()
		var offsetY = 50f

		val drawList = ImGui.getWindowDrawList()
		for ((index, side) in ControllerSide.entries.withIndex()) {
			val length = 100f

			val offsetX = index * (length + spacing)
			val rootX = pos.x + length / 2f + offsetX
			val rootY = pos.y + offsetY

			val triggerValue = when (side) {
				ControllerSide.LEFT -> Input.controllerLeftTrigger
				ControllerSide.RIGHT -> Input.controllerRightTrigger
			}.coerceIn01()

			val dotY = rootY + triggerValue * length

			drawList.addLine(
				ImVec2(rootX, rootY),
				ImVec2(rootX, rootY + length),
				Color.HEX_WHITE
			)
			drawList.addCircleFilled(
				ImVec2(rootX, dotY),
				5f,
				Color.HEX_RED
			)
		}

		offsetY += 125f

		for ((index, side) in ControllerSide.entries.withIndex()) {
			val radius = 50f

			val offsetX = index * (radius * 2 + spacing)
			val centerX = pos.x + radius + offsetX
			val centerY = pos.y + radius + offsetY

			val rawControllerSideValue = when (side) {
				ControllerSide.LEFT -> Input.controllerLeftStick
				ControllerSide.RIGHT -> Input.controllerRightStick
			}
			val stickValue = if (rawControllerSideValue.length() > 1f) rawControllerSideValue.normalize() else rawControllerSideValue
			val dotX = centerX + stickValue.x * radius
			val dotY = centerY - stickValue.y * radius

			fun getLineColor(triggered: Boolean) = if (triggered)
				Color.HEX_CYAN
			else
				Color.HEX_WHITE

			drawList.addCircle(
				ImVec2(centerX, centerY),
				radius,
				getLineColor(stickValue.length().approx(1f, .1f)),
				0,
				2f
			)

			drawList.addLine(
				ImVec2(centerX - radius, centerY),
				ImVec2(centerX + radius, centerY),
				getLineColor(centerY.approx(dotY, 5f)),
				1f
			)
			drawList.addLine(
				ImVec2(centerX, centerY - radius),
				ImVec2(centerX, centerY + radius),
				getLineColor(centerX.approx(dotX, 5f)),
				1f
			)

			drawList.addCircleFilled(
				ImVec2(dotX, dotY),
				5f,
				Color.HEX_RED
			)
		}

		offsetY += 75f

		ImGui.setCursorPosY(pos.y + offsetY)

		ImGui.text("Pressed buttons:")
		for (pressedButton in Input.getControllerButtons())
			ImGui.bulletText(pressedButton.displayName)
	}

}