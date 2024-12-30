package org.emberstudios.input

import org.emberstudios.core.input.InputManager
import org.joml.Vector2f
import org.joml.minus

object Input : InputManager {

	enum class Axis {
		HORIZONTAL,
		VERTICAL
	}

	private val keyboard = KeyboardListener()
	private val mouse = MouseListener()

	val mousePosition get() = mouse.position
	val mouseLastPosition get() = mouse.lastPosition
	val mouseDelta get() = mousePosition - mouseLastPosition
	val mouseScrollDelta get() = mouse.scrollDelta

	fun endFrame() {
		keyboard.endFrame()
		mouse.endFrame()
	}

	// Keyboard
	fun getKey(key: Int) = keyboard.getKey(key)
	fun getKeyDown(key: Int) = keyboard.getKeyDown(key)
	fun getKeyUp(key: Int) = keyboard.getKeyUp(key)

	fun getAxis(axis: Axis) = when (axis) {
		Axis.HORIZONTAL -> {
			val left = getKey(KeyCode.A) || getKey(KeyCode.LEFT)
			val right = getKey(KeyCode.D) || getKey(KeyCode.RIGHT)

			when {
				left && !right -> -1f
				right && !left -> 1f
				else -> 0f
			}
		}

		Axis.VERTICAL -> {
			val down = getKey(KeyCode.S) || getKey(KeyCode.DOWN)
			val up = getKey(KeyCode.W) || getKey(KeyCode.UP)

			when {
				down && !up -> -1f
				up && !down -> 1f
				else -> 0f
			}
		}
	}

	fun getAxes() = Vector2f(
		getAxis(Axis.HORIZONTAL),
		getAxis(Axis.VERTICAL)
	)

	// Mouse
	fun getMouseButton(button: Int) = mouse.getButton(button)
	fun getMouseButtonDown(button: Int) = mouse.getButtonDown(button)
	fun getMouseButtonUp(button: Int) = mouse.getButtonUp(button)

	override fun getKeyboardCallback() = keyboard
	override fun getMouseCallback() = mouse

}