package org.emberstudios.window.glfw

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.WindowHandle
import org.emberstudios.core.nullptr
import org.emberstudios.input.Input
import org.emberstudios.window.Window
import org.joml.Vector2i
import org.lwjgl.glfw.GLFW.*

internal class GLFWWindow : Window {

	companion object {
		val LOGGER = KotlinLogging.logger("Window")
	}

	private var handle: WindowHandle = 0L

	override val size: Vector2i
		get() {
			val pWidth = IntArray(1)
			val pHeight = IntArray(1)
			glfwGetWindowSize(handle, pWidth, pHeight)
			return Vector2i(pWidth[0], pHeight[0])
		}

	override val shouldClose get() = glfwWindowShouldClose(handle)
	override val nativeHandle get() = handle
	override val dpiScale: Float
		get() {
			val pWindowWidth = IntArray(1)
			val pWindowHeight = IntArray(1)
			val pFramebufferWidth = IntArray(1)
			val pFramebufferHeight = IntArray(1)

			glfwGetWindowSize(handle, pWindowWidth, pWindowHeight)
			glfwGetFramebufferSize(handle, pFramebufferWidth, pFramebufferHeight)

			val scaleX = pFramebufferWidth[0].toFloat() / pWindowWidth[0]
			val scaleY = pFramebufferHeight[0].toFloat() / pWindowHeight[0]

			return (scaleX + scaleY) / 2f
		}
	override val time get() = glfwGetTime().toFloat()
	override var lockedCursor: Boolean
		get() = glfwGetInputMode(handle, GLFW_CURSOR) == GLFW_CURSOR_DISABLED
		set(value) {
			val mode = if (value) GLFW_CURSOR_DISABLED else GLFW_CURSOR_NORMAL
			glfwSetInputMode(handle, GLFW_CURSOR, mode)
		}

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

		glfwSetKeyCallback(handle) { _, key, _, action, _ ->
			Input.keyboard.onKey(key, action) }
		glfwSetMouseButtonCallback(handle) { _, button, action, _ ->
			Input.mouse.onMouseButton(button, action) }
		glfwSetCursorPosCallback(handle) { _, posX, posY ->
			Input.mouse.onMouseMove(posX.toFloat(), posY.toFloat()) }
		glfwSetScrollCallback(handle) { _, offsetX, offsetY ->
			Input.mouse.onScroll(offsetX.toFloat(), offsetY.toFloat()) }

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

	override fun quit() = glfwSetWindowShouldClose(handle, true)

	override fun setResizeCallback(callback: (Int, Int) -> Unit) {
		glfwSetFramebufferSizeCallback(handle) { _, width, height -> callback(width, height)}
	}

	override fun createRenderContext() = GLFWContext(handle)

}