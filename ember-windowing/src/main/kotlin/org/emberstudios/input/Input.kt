package org.emberstudios.input

import org.joml.Vector2f
import org.joml.minus

object Input {

	enum class Axis {
		HORIZONTAL,
		VERTICAL
	}

	internal val keyboard = KeyboardListener()
	internal val mouse = MouseListener()
	internal val controller = ControllerListener()

	val mousePosition get() = mouse.position
	val mouseLastPosition get() = mouse.lastPosition
	val mouseDelta get() = mousePosition - mouseLastPosition
	val mouseScrollDelta get() = if (blockMouse) Vector2f() else mouse.scrollDelta

	val controllerLeftStick: Vector2f get() {
		val stick = controller.leftStick
		return if (stick.length() > controllerStickDeadzone) stick else Vector2f()
	}
	val controllerRightStick: Vector2f get() {
		val stick = controller.rightStick
		return if (stick.length() > controllerStickDeadzone) stick else Vector2f()
	}
	val controllerLeftTrigger get() = controller.leftTrigger
	val controllerRightTrigger get() = controller.rightTrigger

	var blockingCallback: () -> Pair<Boolean, Boolean> = { false to false }
	var mouseViewportPosition = Vector2f(-1f, -1f)
	var controllerStickDeadzone = 0.1f

	private val blockKeyboard get() = blockingCallback().first
	private val blockMouse get() = blockingCallback().second

	fun endFrame() {
		keyboard.endFrame()
		mouse.endFrame()
		controller.endFrame()
	}

	// Keyboard
	fun getKey(key: Key) = keyboard.getKey(key) && !blockKeyboard
	fun getKeyDown(key: Key) = keyboard.getKeyDown(key) && !blockKeyboard
	fun getKeyUp(key: Key) = keyboard.getKeyUp(key) && !blockKeyboard

	fun getAxis(axis: Axis) = when (axis) {
		Axis.HORIZONTAL -> {
			val left = getKey(Key.A) || getKey(Key.LEFT)
			val right = getKey(Key.D) || getKey(Key.RIGHT)

			when {
				left && !right -> -1f
				right && !left -> 1f
				else -> 0f
			}
		}

		Axis.VERTICAL -> {
			val down = getKey(Key.S) || getKey(Key.DOWN)
			val up = getKey(Key.W) || getKey(Key.UP)

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

	// Controller
	fun getControllerButton(button: ControllerButton) = controller.getButton(button)
	fun getControllerButtonDown(button: ControllerButton) = controller.getButtonDown(button)
	fun getControllerButtonUp(button: ControllerButton) = controller.getButtonUp(button)

	fun getControllerButtons() = controller.getButtons()

	fun setControllerVibration(leftMotor: Float, rightMotor: Float) =
		controller.setVibration(leftMotor, rightMotor)

}