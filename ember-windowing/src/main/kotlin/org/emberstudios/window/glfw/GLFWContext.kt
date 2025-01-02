package org.emberstudios.window.glfw

import org.emberstudios.core.WindowHandle
import org.emberstudios.renderer.RenderContext
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import java.io.File
import java.io.PrintStream

class GLFWContext(private val windowHandle: WindowHandle) : RenderContext {

	override fun init() {
		glfwMakeContextCurrent(windowHandle)
	}

	override fun initLog(logDir: String) {
		val logFile = File("$logDir/glfw_error.log")
		logFile.parentFile.mkdirs()

		GLFWErrorCallback.createPrint(PrintStream(logFile.outputStream())).set()
	}

	override fun swapBuffers() = glfwSwapBuffers(windowHandle)
	override fun makeContextCurrent(windowHandle: WindowHandle) = glfwMakeContextCurrent(windowHandle)

	override fun getCurrentContext() = glfwGetCurrentContext()

}