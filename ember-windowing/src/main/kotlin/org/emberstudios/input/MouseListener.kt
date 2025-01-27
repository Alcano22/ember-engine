package org.emberstudios.input

import org.emberstudios.core.logger.getLogger
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

internal class MouseListener {

	companion object {
		const val NUM_BUTTONS = 5

		val LOGGER = getLogger<MouseListener>()
	}

	private val pressedButtons = BooleanArray(NUM_BUTTONS)
	private val currentButtons = BooleanArray(NUM_BUTTONS)
	private val downButtons = BooleanArray(NUM_BUTTONS)
	private val upButtons = BooleanArray(NUM_BUTTONS)

	var dragging = false
		private set
	var position = Vector2f()
		private set
	var lastPosition = Vector2f()
		private set
	var scrollDelta = Vector2f()
		private set

	fun endFrame() {
		scrollDelta.set(0f)
		lastPosition.set(0f)

		for (i in 0..<NUM_BUTTONS) {
			upButtons[i] = !pressedButtons[i] && currentButtons[i]
			downButtons[i] = pressedButtons[i] && !currentButtons[i]
			currentButtons[i] = pressedButtons[i]
		}
	}

	fun onMouseButton(button: Int, action: Int) {
		if (button >= NUM_BUTTONS) return

		when (action) {
			GLFW_PRESS -> pressedButtons[button] = true
			GLFW_RELEASE -> {
				pressedButtons[button] = false
				dragging = false
			}
		}
	}

	fun onMouseMove(x: Float, y: Float) {
		lastPosition.set(x, y)
		position.set(x, y)

		for (i in 0..<NUM_BUTTONS) {
			if (pressedButtons[i])
				dragging = true
		}
	}

	fun onScroll(offsetX: Float, offsetY: Float) {
		scrollDelta.set(offsetX, offsetY)
	}

	fun getButton(button: Int): Boolean {
		if (button >= NUM_BUTTONS) {
			LOGGER.warn { "Invalid mouse button: '$button'" }
			return false
		}

		return currentButtons[button]
	}

	fun getButtonDown(button: Int): Boolean {
		if (button >= NUM_BUTTONS) {
			LOGGER.warn { "Invalid mouse button: '$button'" }
			return false
		}

		return downButtons[button]
	}

	fun getButtonUp(button: Int): Boolean {
		if (button >= NUM_BUTTONS) {
			LOGGER.warn { "Invalid mouse button: '$button'" }
			return false
		}

		return upButtons[button]
	}
}