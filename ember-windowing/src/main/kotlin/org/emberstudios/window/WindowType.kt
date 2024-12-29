package org.emberstudios.window

import org.emberstudios.window.glfw.GLFWWindow

enum class WindowType(private val windowFactory: () -> Window) {
	GLFW({ GLFWWindow() });

	fun create() = windowFactory()
}