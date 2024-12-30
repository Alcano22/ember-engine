package org.emberstudios.renderer.opengl

import org.emberstudios.core.nullptr
import org.emberstudios.renderer.RenderAPI
import org.emberstudios.renderer.VertexArray
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL43.*
import org.lwjgl.opengl.GLDebugMessageCallback
import org.lwjgl.system.MemoryUtil
import java.io.File

internal class GLRenderAPI : RenderAPI {

	override fun init() {
		GL.createCapabilities()


		glEnable(GL_DEPTH_TEST)

		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
	}

	override fun initLog(logDir: String) {
		val logFile = File("$logDir/opengl_debug.log")
		logFile.parentFile.mkdirs()

		glEnable(GL_DEBUG_OUTPUT)
		glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS)

		glDebugMessageCallback(GLDebugMessageCallback.create { src, type, id, severity, length, message, _ ->
			val msg = MemoryUtil.memASCII(message, length)
			logFile.appendText("[OpenGL Debug] Source: $src, Type: $type, ID: $id, Severity: $severity\nMessage: $msg\n")
		}, nullptr)
	}

	override fun clear(r: Float, g: Float, b: Float, a: Float) {
		glClearColor(r, g, b, a)
		glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
	}

	override fun viewport(width: Int, height: Int) {
		glViewport(0, 0, width, height)
	}

	override fun drawIndexed(vertexArray: VertexArray) {
		glDrawElements(GL_TRIANGLES, vertexArray.getIndexBuffer()!!.count, GL_UNSIGNED_INT, nullptr)
	}

}