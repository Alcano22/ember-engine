package org.emberstudios.window.glfw

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.nullptr
import org.emberstudios.renderer.RenderContext
import org.emberstudios.window.Window
import org.emberstudios.window.WindowHandle
import org.joml.Vector2i
import org.lwjgl.glfw.GLFW.*

internal class GLFWWindow : Window {

	companion object {
		val LOGGER = KotlinLogging.logger("Window")
	}

	private var handle: WindowHandle = 0L

	override val nativeHandle: Long
		get() = handle

	override val size: Vector2i
		get() {
			val pWidth = IntArray(1)
			val pHeight = IntArray(1)
			glfwGetWindowSize(handle, pWidth, pHeight)
			return Vector2i(pWidth[0], pHeight[0])
		}

	override val shouldClose: Boolean
		get() = glfwWindowShouldClose(handle)

	override val time: Float
		get() = glfwGetTime().toFloat()

	override fun init(title: String, width: Int, height: Int): Boolean {
		if (!glfwInit()) {
			LOGGER.error { "Failed to initialize GLFW!" }
			return false
		}

		glfwDefaultWindowHints()
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

		handle = glfwCreateWindow(width, height, title, nullptr, nullptr)
		if (handle == nullptr) {
			LOGGER.error { "Failed to create GLFW window!" }
			glfwTerminate()
			return false
		}

		glfwSwapInterval(1)
		glfwShowWindow(handle)

		return true
	}

	override fun update() {
		glfwPollEvents()
	}

	override fun destroy() {
		glfwDestroyWindow(handle)
		glfwTerminate()
	}

	override fun setResizeCallback(callback: (Int, Int) -> Unit) {
		glfwSetFramebufferSizeCallback(handle) { _, width, height -> callback(width, height)}
	}

	override fun createRenderContext() = GLFWContext(handle)

}