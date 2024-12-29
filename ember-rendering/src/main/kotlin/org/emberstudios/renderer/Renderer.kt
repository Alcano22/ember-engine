package org.emberstudios.renderer

import org.emberstudios.core.logger.getLogger
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector4f

object Renderer {

	private val LOGGER = getLogger<Renderer>()

	lateinit var apiType: RenderAPIType
		private set
	private lateinit var api: RenderAPI

	private var initialized = false

	fun init(apiType: RenderAPIType, context: RenderContext, renderLogDir: String, windowLogDir: String) {
		if (initialized) {
			LOGGER.warn { "Renderer is already initialized!" }
			return
		}

		context.init()
		context.initLog(windowLogDir)

		this.apiType = apiType
		api = apiType.create()
		api.init()
		api.initLog(renderLogDir)

		initialized = true
	}

	fun clear(r: Float, g: Float, b: Float, a: Float) = api.clear(r, g, b, a)
	fun clear(color: Vector4f) = clear(color.x, color.y, color.z, color.w)

	fun viewport(width: Int, height: Int) = api.viewport(width, height)
	fun viewport(size: Vector2i) = viewport(size.x, size.y)

	fun drawIndexed(vertexArray: VertexArray) = api.drawIndexed(vertexArray)

	fun swapBuffers(context: RenderContext) = context.swapBuffers()

	fun submit(
		shader: Shader,
		vertexArray: VertexArray,
		uniforms: Map<String, Any> = emptyMap()
	) {
		shader.bind()

		for ((name, value) in uniforms)
			shader.setUniform(name, value)

		vertexArray.bind()
		drawIndexed(vertexArray)
		vertexArray.unbind()

		shader.unbind()
	}

}