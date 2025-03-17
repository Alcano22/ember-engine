package org.emberstudios.engine.gameobject.component

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.emberstudios.core.io.ResourceManager
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.util.Color
import org.emberstudios.renderer.*

@Serializable
class SpriteRenderer : Component() {

	companion object {
		val INDICES = intArrayOf(
			0, 1, 2,
			2, 3, 0
		)
	}

	@Contextual var texture: Texture? = null
	var color = Color.WHITE

	@Transient private lateinit var shader: Shader
	@Transient private lateinit var vao: VertexArray

	override fun init() {
		shader = ResourceManager.loadShader("assets\\shaders\\default.glsl")
		shader.compile()

		vao = VertexArray.create()

		val (u0, v0, u1, v1) = texture?.uv ?: floatArrayOf(0f, 0f, 1f, 1f)
		val vertices = floatArrayOf(
			// Position         TexCoord
			 .5f,  .5f, 		u1, v1,
			 .5f, -.5f, 		u1, v0,
			-.5f, -.5f, 		u0, v0,
			-.5f,  .5f, 		u0, v1
		)

		val vbo = VertexBuffer.create(vertices)
		vbo.layout = bufferLayout {
			float2("a_Position")
			float2("a_TexCoord")
		}
		vao.addVertexBuffer(vbo)

		val ebo = IndexBuffer.create(INDICES)
		vao.setIndexBuffer(ebo)
	}

	override fun render() = Renderer.submit(
		shader,
		vao,
		transform.toMatrix(),
		texture,
		color
	)

}