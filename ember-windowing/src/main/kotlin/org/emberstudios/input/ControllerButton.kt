package org.emberstudios.input

import com.github.strikerx3.jxinput.enums.XInputButton
import org.emberstudios.core.util.toDisplayStyle

/**
 * Repesents a button on a controller.
 */
enum class ControllerButton(val code: Int) {
    A(XInputButton.A.ordinal),
    B(XInputButton.B.ordinal),
    X(XInputButton.X.ordinal),
    Y(XInputButton.Y.ordinal),
    LB(XInputButton.LEFT_SHOULDER.ordinal),
    RB(XInputButton.RIGHT_SHOULDER.ordinal),
    SELECT(XInputButton.BACK.ordinal),
    START(XInputButton.START.ordinal),
    LS(XInputButton.LEFT_THUMBSTICK.ordinal),
    RS(XInputButton.RIGHT_THUMBSTICK.ordinal),
    DPAD_UP(XInputButton.DPAD_UP.ordinal),
    DPAD_DOWN(XInputButton.DPAD_DOWN.ordinal),
    DPAD_LEFT(XInputButton.DPAD_LEFT.ordinal),
    DPAD_RIGHT(XInputButton.DPAD_RIGHT.ordinal);

    /**
     * The display name of the button.
     */
    val displayName = name.toDisplayStyle()
}
