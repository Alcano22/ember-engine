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
	val mouseScrollDelta get() = if (blockMouse) Vector2f() else mouse.scrollDelta

	var blockingCallback: () -> Pair<Boolean, Boolean> = { false to false }
	var mouseViewportPosition = Vector2f(-1f, -1f)

	private val blockKeyboard get() = blockingCallback().first
	private val blockMouse get() = blockingCallback().second

	fun endFrame() {
		keyboard.endFrame()
		mouse.endFrame()
	}

	// Keyboard
	fun getKey(key: Int) = keyboard.getKey(key) && !blockKeyboard
	fun getKeyDown(key: Int) = keyboard.getKeyDown(key) && !blockKeyboard
	fun getKeyUp(key: Int) = keyboard.getKeyUp(key) && !blockKeyboard

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
	fun getMouseButton(button: Int) = mouse.getButton(button) && !blockMouse
	fun getMouseButtonDown(button: Int) = mouse.getButtonDown(button) && !blockMouse
	fun getMouseButtonUp(button: Int) = mouse.getButtonUp(button) && !blockMouse

	override fun getKeyboardCallback() = keyboard
	override fun getMouseCallback() = mouse

}