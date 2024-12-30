package org.emberstudios.window

import org.emberstudios.core.input.InputManager
import org.emberstudios.core.input.MouseCallback
import org.emberstudios.window.glfw.GLFWInputHandler
import org.emberstudios.window.glfw.GLFWWindow

enum class WindowAPIType(
	private val windowFactory: () -> Window,
	private val inputHandlerFactory: (InputManager) -> InputHandler
) {
	GLFW({ GLFWWindow() }, { GLFWInputHandler(it) });

	internal fun createWindow() = windowFactory()
	internal fun createInputHandler(inputManager: InputManager) = inputHandlerFactory(inputManager)
}