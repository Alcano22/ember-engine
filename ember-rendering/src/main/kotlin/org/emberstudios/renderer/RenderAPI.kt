package org.emberstudios.renderer

interface RenderAPI {
	fun init()
	fun initLog(logDir: String)

	fun clear(r: Float, g: Float, b: Float, a: Float)
	fun viewport(width: Int, height: Int)

	fun drawIndexed(vertexArray: VertexArray)
}