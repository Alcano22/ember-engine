package org.emberstudios.renderer

import org.emberstudios.core.WindowHandle

interface RenderContext {

	fun init()
	fun initLog(logDir: String)

	fun swapBuffers()
	fun makeContextCurrent(windowHandle: WindowHandle)

	fun getCurrentContext(): WindowHandle

}