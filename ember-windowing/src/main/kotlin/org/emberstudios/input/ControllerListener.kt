package org.emberstudios.input

import com.github.strikerx3.jxinput.XInputButtons
import com.github.strikerx3.jxinput.XInputDevice
import com.github.strikerx3.jxinput.enums.XInputButton
import org.emberstudios.core.logger.getLogger
import org.joml.Vector2f

internal class ControllerListener {

    companion object {
        val NUM_BUTTONS = XInputButton.entries.size

        val LOGGER = getLogger<ControllerListener>()
    }

    private val device = XInputDevice.getDeviceFor(0)

    private val pressedButtons = BooleanArray(NUM_BUTTONS)
    private val currentButtons = BooleanArray(NUM_BUTTONS)
    private val downButtons = BooleanArray(NUM_BUTTONS)
    private val upButtons = BooleanArray(NUM_BUTTONS)

    var leftStick = Vector2f()
        private set
    var rightStick = Vector2f()
        private set
    var leftTrigger = 0f
        private set
    var rightTrigger = 0f
        private set

    var connected = false
        private set

    fun endFrame() = device?.let {
        if (!it.poll()) {
            connected = false
            return@let
        }

        connected = true

        val components = it.components
        val buttonStates = components.buttons

        for (button in XInputButton.entries) {
            val index = button.ordinal
            val pressed = getButtonState(buttonStates, button)

            upButtons[index] = !pressed && currentButtons[index]
            downButtons[index] = pressed && !currentButtons[index]
            currentButtons[index] = pressed
            pressedButtons[index] = pressed
        }

        leftStick.set(components.axes.lx, components.axes.ly)
        rightStick.set(components.axes.rx, components.axes.ry)
        leftTrigger = components.axes.lt
        rightTrigger = components.axes.rt
    }

    private fun getButtonState(buttons: XInputButtons, button: XInputButton) = when (button) {
        XInputButton.A -> buttons.a
        XInputButton.B -> buttons.b
        XInputButton.X -> buttons.x
        XInputButton.Y -> buttons.y
        XInputButton.BACK -> buttons.back
        XInputButton.START -> buttons.start
        XInputButton.LEFT_SHOULDER -> buttons.lShoulder
        XInputButton.RIGHT_SHOULDER -> buttons.rShoulder
        XInputButton.LEFT_THUMBSTICK -> buttons.lThumb
        XInputButton.RIGHT_THUMBSTICK -> buttons.rThumb
        XInputButton.DPAD_UP -> buttons.up
        XInputButton.DPAD_DOWN -> buttons.down
        XInputButton.DPAD_LEFT -> buttons.left
        XInputButton.DPAD_RIGHT -> buttons.right
        XInputButton.GUIDE_BUTTON -> buttons.guide
        XInputButton.UNKNOWN -> false
    }

    fun getButton(button: ControllerButton) = currentButtons[button.code]
    fun getButtonDown(button: ControllerButton) = downButtons[button.code]
    fun getButtonUp(button: ControllerButton) = upButtons[button.code]

    fun getButtons(): List<ControllerButton> {
        val buttons = mutableListOf<ControllerButton>()
        for (button in ControllerButton.entries) {
            if (currentButtons[button.code])
                buttons += button
        }
        return buttons
    }

    fun setVibration(leftMotor: Float, rightMotor: Float) = device?.let {
        val max = 65535
        val leftSpeed = (leftMotor * max).toInt().coerceIn(0, max)
        val rightSpeed = (rightMotor * max).toInt().coerceIn(0, max)
        it.setVibration(leftSpeed, rightSpeed)
    }

}