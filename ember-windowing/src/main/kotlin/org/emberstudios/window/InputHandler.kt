package org.emberstudios.window

import org.emberstudios.core.input.InputManager

interface InputHandler {
	companion object {
		fun create(inputManager: InputManager) = Window.apiType.createInputHandler(inputManager)
	}

	fun init(windowHandle: WindowHandle)
}