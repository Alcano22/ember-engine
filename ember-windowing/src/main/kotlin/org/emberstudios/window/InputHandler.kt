package org.emberstudios.window

import org.emberstudios.core.WindowHandle
import org.emberstudios.core.input.InputManager
import org.emberstudios.core.window.WindowAPIType
import org.emberstudios.window.glfw.GLFWInputHandler

interface InputHandler {
	companion object {
		fun create(inputManager: InputManager): InputHandler = when (Window.apiType) {
			WindowAPIType.GLFW -> GLFWInputHandler(inputManager)
		}
	}

	fun init(windowHandle: WindowHandle)
}