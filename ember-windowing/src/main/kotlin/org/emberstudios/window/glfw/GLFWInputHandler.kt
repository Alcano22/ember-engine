package org.emberstudios.window.glfw

import org.emberstudios.core.WindowHandle
import org.emberstudios.core.input.InputAction
import org.emberstudios.core.input.InputManager
import org.emberstudios.core.logger.exitError
import org.emberstudios.core.logger.getLogger
import org.emberstudios.window.InputHandler
import org.lwjgl.glfw.GLFW.*

internal class GLFWInputHandler(inputManager: InputManager) : InputHandler {

	companion object {
		private val LOGGER = getLogger<GLFWInputHandler>()
	}

	private val keyboardCallback = inputManager.getKeyboardCallback()
	private val mouseCallback = inputManager.getMouseCallback()

	override fun init(windowHandle: WindowHandle) {
		glfwSetKeyCallback(windowHandle) { _, key, _, action, _ ->
			keyboardCallback.onKey(key, glfwActionToEmberAction(action))
		}

		glfwSetMouseButtonCallback(windowHandle) { _, button, action, _ ->
			mouseCallback.onMouseButton(button, glfwActionToEmberAction(action))
		}

		glfwSetCursorPosCallback(windowHandle) { _, xpos, ypos ->
			mouseCallback.onMouseMove(xpos.toFloat(), ypos.toFloat())
		}

		glfwSetScrollCallback(windowHandle) { _, xoffset, yoffset ->
			mouseCallback.onScroll(xoffset.toFloat(), yoffset.toFloat())
		}
	}

	private fun glfwActionToEmberAction(glfwAction: Int) = when (glfwAction) {
		GLFW_PRESS, GLFW_REPEAT -> InputAction.PRESS
		GLFW_RELEASE -> InputAction.RELEASE
		else -> LOGGER.exitError { "Unknown glfw action: $glfwAction" }
	}

}