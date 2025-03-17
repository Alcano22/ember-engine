package org.emberstudios.input

import org.joml.Vector2f
import org.joml.minus

/**
 * Wrapper for input handling.
 */
object Input {

	/**
	 * Enum for input axes.
	 */
	enum class Axis {
		HORIZONTAL,
		VERTICAL
	}

	internal val keyboard = KeyboardListener()
	internal val mouse = MouseListener()
	internal val controller = ControllerListener()

	/**
	 * Returns the current mouse position.
	 */
	val mousePosition get() = mouse.position

	/**
	 * Returns the last mouse position.
	 */
	val mouseLastPosition get() = mouse.lastPosition

	/**
	 * Returns the delta between the current and last mouse position.
	 */
	val mouseDelta get() = mousePosition - mouseLastPosition

	/**
	 * Returns the current mouse scroll delta.
	 */
	val mouseScrollDelta get() = if (blockMouse) Vector2f() else mouse.scrollDelta

	/**
	 * Returns the current left stick values of the controller.
	 */
	val controllerLeftStick: Vector2f get() {
		val stick = controller.leftStick
		return if (stick.length() > controllerStickDeadzone) stick else Vector2f()
	}

	/**
	 * Returns the current right stick values of the controller.
	 */
	val controllerRightStick: Vector2f get() {
		val stick = controller.rightStick
		return if (stick.length() > controllerStickDeadzone) stick else Vector2f()
	}

	/**
	 * Returns the current left trigger value of the controller.
	 */
	val controllerLeftTrigger get() = controller.leftTrigger

	/**
	 * Returns the current right trigger value of the controller.
	 */
	val controllerRightTrigger get() = controller.rightTrigger

	/**
	 * The callback to check if input should be blocked.
	 */
	var blockingCallback: () -> Pair<Boolean, Boolean> = { false to false }

	/**
	 * Mouse position in viewport coordinates.
	 */
	var mouseViewportPosition = Vector2f(-1f, -1f)

	/**
	 * Deadzone for controller sticks.
	 */
	var controllerStickDeadzone = 0.1f

	private val blockKeyboard get() = blockingCallback().first
	private val blockMouse get() = blockingCallback().second

	/**
	 * Ends the current input frame.
	 */
	fun endFrame() {
		keyboard.endFrame()
		mouse.endFrame()
		controller.endFrame()
	}

	// Keyboard
	/**
	 * Returns whether the specified key is currently pressed.
	 */
	fun getKey(key: Key) = keyboard.getKey(key) && !blockKeyboard

	/**
	 * Returns whether the specified key was pressed this frame.
	 */
	fun getKeyDown(key: Key) = keyboard.getKeyDown(key) && !blockKeyboard

	/**
	 * Returns whether the specified key was released this frame.
	 */
	fun getKeyUp(key: Key) = keyboard.getKeyUp(key) && !blockKeyboard

	/**
	 * Returns the current state of all keys.
	 */
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

	/**
	 * Returns the current state of all axes.
	 */
	fun getAxes() = Vector2f(
		getAxis(Axis.HORIZONTAL),
		getAxis(Axis.VERTICAL)
	)

	// Mouse
	/**
	 * Returns whether the specified mouse button is currently pressed.
	 */
	fun getMouseButton(button: Int) = mouse.getButton(button) && !blockMouse

	/**
	 * Returns whether the specified mouse button was pressed this frame.
	 */
	fun getMouseButtonDown(button: Int) = mouse.getButtonDown(button) && !blockMouse

	/**
	 * Returns whether the specified mouse button was released this frame.
	 */
	fun getMouseButtonUp(button: Int) = mouse.getButtonUp(button) && !blockMouse

	// Controller
	/**
	 * Returns whether the specified controller button is currently pressed.
	 */
	fun getControllerButton(button: ControllerButton) = controller.getButton(button)

	/**
	 * Returns whether the specified controller button was pressed this frame.
	 */
	fun getControllerButtonDown(button: ControllerButton) = controller.getButtonDown(button)

	/**
	 * Returns whether the specified controller button was released this frame.
	 */
	fun getControllerButtonUp(button: ControllerButton) = controller.getButtonUp(button)

	/**
	 * Returns the current state of all controller buttons.
	 */
	fun getControllerButtons() = controller.getButtons()

	/**
	 * Sets the vibration of the controller.
	 */
	fun setControllerVibration(leftMotor: Float, rightMotor: Float) =
		controller.setVibration(leftMotor, rightMotor)

}