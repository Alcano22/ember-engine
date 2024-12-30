package org.emberstudios.input

import org.emberstudios.core.input.InputAction
import org.emberstudios.core.input.MouseCallback
import org.emberstudios.core.logger.getLogger
import org.joml.Vector2f

class MouseListener : MouseCallback {

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

	override fun onMouseButton(button: Int, action: InputAction) {
		if (button >= NUM_BUTTONS) return

		when (action) {
			InputAction.PRESS -> pressedButtons[button] = true
			InputAction.RELEASE -> {
				pressedButtons[button] = false
				dragging = false
			}
		}
	}

	override fun onMouseMove(x: Float, y: Float) {
		lastPosition.set(x, y)
		position.set(x, y)

		for (i in 0..<NUM_BUTTONS) {
			if (pressedButtons[i])
				dragging = true
		}
	}

	override fun onScroll(offsetX: Float, offsetY: Float) {
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