package org.emberstudios.renderer

import org.emberstudios.core.logger.getLogger
import org.emberstudios.core.renderer.GraphicsAPIType
import org.joml.Matrix4f
import org.joml.Vector2i
import org.joml.Vector4f

object Renderer {

	private val LOGGER = getLogger<Renderer>()

	lateinit var apiType: GraphicsAPIType
		private set
	private lateinit var api: RenderAPI

	private var camera: Camera? = null

	private var initialized = false

	fun init(apiType: GraphicsAPIType, context: RenderContext, renderLogDir: String, windowLogDir: String) {
		if (initialized) {
			LOGGER.warn { "Renderer is already initialized!" }
			return
		}

		context.init()
		context.initLog(windowLogDir)

		this.apiType = apiType
		api = RenderAPI.create(apiType)
		api.init()
		api.initLog(renderLogDir)

		initialized = true
	}

	fun clear(r: Float, g: Float, b: Float, a: Float) = api.clear(r, g, b, a)
	fun clear(color: Vector4f) = clear(color.x, color.y, color.z, color.w)

	fun viewport(width: Int, height: Int) = api.viewport(width, height)
	fun viewport(size: Vector2i) = viewport(size.x, size.y)

	fun drawIndexed(vertexArray: VertexArray) = api.drawIndexed(vertexArray)

	fun beginScene(camera: Camera) {
		this.camera = camera
	}

	fun endScene() {
		camera = null
	}

	fun submit(
		shader: Shader,
		vertexArray: VertexArray,
		transformMatrix: Matrix4f,
		texture: Texture? = null,
		color: Vector4f = Vector4f(1f),
		uniforms: Map<String, Any> = emptyMap()
	) {
		if (camera == null) {
			LOGGER.error { "Camera is null. This can be due to not calling beginScene!" }
			return
		}

		shader.bind()

		for ((name, value) in uniforms)
			shader.setUniform(name, value)

		if (texture != null) {
			texture.activate(0)
			texture.bind()
			shader.setUniform("u_Texture", 0)
			shader.setUniform("u_UseTexture", true)
		} else
			shader.setUniform("u_UseTexture", false)

		shader.setUniform("u_Color", color)

		shader.setUniform("u_ViewProjection", camera!!.viewProjectionMatrix)
		shader.setUniform("u_Transform", transformMatrix)

		vertexArray.bind()
		drawIndexed(vertexArray)
		vertexArray.unbind()

		texture?.unbind()
		texture?.deactivate(0)

		shader.unbind()
	}

}