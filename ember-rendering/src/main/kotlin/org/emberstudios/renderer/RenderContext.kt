package org.emberstudios.renderer

interface RenderContext {

	fun init()
	fun initLog(logDir: String)

	fun swapBuffers()

}