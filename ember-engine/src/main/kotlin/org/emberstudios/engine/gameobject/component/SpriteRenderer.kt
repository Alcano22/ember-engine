package org.emberstudios.engine.gameobject.component

import org.emberstudios.core.io.ResourceManager
import org.emberstudios.core.math.toArray
import org.emberstudios.renderer.*
import org.joml.Vector4f

class SpriteRenderer : Component() {

	var texture: Texture? = null
	var color = Vector4f(1f)

	private lateinit var shader: Shader

	override fun init() {
		shader = ResourceManager.loadShader("assets/shaders/default.glsl")
		shader.compile()
	}

	override fun render() {
		val pos = transform.position
		val scale = transform.scale
		val col = color.toArray()
		val vertices = floatArrayOf(
			// Position                                         Color		TexCoord
			pos.x + scale.x * -.5f, pos.y + scale.y *  .5f,     *col,		0f, 1f,
			pos.x + scale.x *  .5f, pos.y + scale.y *  .5f,     *col,		1f, 1f,
			pos.x + scale.x * -.5f, pos.y + scale.y * -.5f,     *col,		0f, 0f,
			pos.x + scale.x *  .5f, pos.y + scale.y * -.5f,     *col,		1f, 0f
		)

		val indices = intArrayOf(
			0, 1, 2,
			1, 3, 2
		)

		val vao = VertexArray.create()
		val vbo = VertexBuffer.create()
		val ebo = IndexBuffer.create(indices)

		vbo.setVertices(vertices)
		vbo.layout = bufferLayout {
			element(ShaderDataType.FLOAT2, "a_Position")
			element(ShaderDataType.FLOAT4, "a_Color")
			element(ShaderDataType.FLOAT2, "a_TexCoord")
		}

		vao.addVertexBuffer(vbo)
		vao.setIndexBuffer(ebo)

		Renderer.submit(
			shader,
			vao,
			transform.toMatrix(),
			texture
		)
	}

}