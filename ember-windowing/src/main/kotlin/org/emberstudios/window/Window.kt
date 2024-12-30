package org.emberstudios.window

import org.emberstudios.renderer.RenderContext
import org.joml.Vector2i

interface Window {

	companion object {
		lateinit var apiType: WindowAPIType

		fun create(apiType: WindowAPIType): Window {
			this.apiType = apiType
			return apiType.createWindow()
		}
	}

	val size: Vector2i
	val shouldClose: Boolean
	val nativeHandle: Long
	val time: Float

	fun init(title: String, width: Int, height: Int): Boolean
	fun update()
	fun destroy()

	fun setResizeCallback(callback: (Int, Int) -> Unit)

	fun createRenderContext(): RenderContext

}