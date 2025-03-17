package org.emberstudios.renderer

import org.emberstudios.core.logger.getLogger
import org.emberstudios.core.renderer.GraphicsAPIType
import org.joml.Matrix4f
import org.joml.Vector2i
import org.joml.Vector4f

/**
 * The main renderer class that handles all rendering operations.
 */
object Renderer {

	private val LOGGER = getLogger<Renderer>()

	/**
	 * The graphics API type that the renderer is using.
	 */
	lateinit var apiType: GraphicsAPIType
		private set
	private lateinit var api: RenderAPI

	private var camera: Camera? = null

	private var initialized = false

	/**
	 * Initializes the renderer with the given [apiType], [context], [renderLogDir], and [windowLogDir].
	 *
	 * @param apiType The graphics API type to use.
	 * @param context The render context to use.
	 * @param renderLogDir The directory to store the render logs.
	 * @param windowLogDir The directory to store the window logs.
	 */
	fun init(apiType: GraphicsAPIType, context: RenderContext, renderLogDir: String?, windowLogDir: String?) {
		if (initialized) {
			LOGGER.warn { "Renderer is already initialized!" }
			return
		}

		context.init()
		windowLogDir?.let { context.initLog(it) }

		this.apiType = apiType
		api = RenderAPI.create(apiType)
		api.init()
		renderLogDir?.let { api.initLog(it) }

		initialized = true
	}

	/**
	 * Clears the screen with the given [r], [g], [b] and [a] values.
	 *
	 * @param r The red value.
	 * @param g The green value.
	 * @param b The blue value.
	 * @param a The alpha value.
	 */
	fun clear(r: Float, g: Float, b: Float, a: Float) = api.clear(r, g, b, a)

	/**
	 * Clears the screen with the given [color].
	 *
	 * @param color The color to clear the screen with.
	 */
	fun clear(color: Vector4f) = clear(color.x, color.y, color.z, color.w)

	/**
	 * Sets the viewport to the given [width] and [height].
	 *
	 * @param width The width of the viewport.
	 * @param height The height of the viewport.
	 */
	fun viewport(width: Int, height: Int) = api.viewport(width, height)

	/**
	 * Sets the viewport to the given [size].
	 *
	 * @param size The size of the viewport.
	 */
	fun viewport(size: Vector2i) = viewport(size.x, size.y)

	/**
	 * Draws the given [vertexArray].
	 *
	 * @param vertexArray The vertex array to draw.
	 */
	fun drawIndexed(vertexArray: VertexArray) = api.drawIndexed(vertexArray)

	/**
	 * Begins the scene with the given [camera].
	 *
	 * @param camera The camera to use.
	 */
	fun beginScene(camera: Camera) {
		this.camera = camera
	}

	/**
	 * Ends the scene.
	 */
	fun endScene() {
		camera = null
	}

	/**
	 * Submits the given [shader], [vertexArray], [transformMatrix], [texture], [color], and [uniforms] to be rendered.
	 *
	 * @param shader The shader to use.
	 * @param vertexArray The vertex array to use.
	 * @param transformMatrix The transform matrix to use.
	 * @param texture The texture to use.
	 * @param color The color to use.
	 * @param uniforms The uniforms to use (optional).
	 */
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