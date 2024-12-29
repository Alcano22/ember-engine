package org.emberstudios.engine

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.io.ResourceManager
import org.emberstudios.engine.util.Time
import org.emberstudios.renderer.*
import org.emberstudios.window.WindowType

val LOGGER = KotlinLogging.logger("ENGINE")

fun main() {
	val window = WindowType.GLFW.create()

	if (!window.init("Ember Engine", 1600, 1200))
		return

	window.setResizeCallback { width, height -> Renderer.viewport(width, height)}

	val context = window.createRenderContext()

	Renderer.init(RenderAPIType.OPEN_GL, context, "logs/", "logs/")
	Renderer.viewport(window.size)

	val vertices = floatArrayOf(
		// Position         Color						TexCoord
		-0.5f,  0.5f,       1.0f, 1.0f, 1.0f, 1.0f,		0.0f, 1.0f,
		 0.5f,  0.5f,       1.0f, 1.0f, 1.0f, 1.0f,		1.0f, 1.0f,
		-0.5f, -0.5f,       1.0f, 1.0f, 1.0f, 1.0f,		0.0f, 0.0f,
		 0.5f, -0.5f,       1.0f, 1.0f, 1.0f, 1.0f,		1.0f, 0.0f
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

	val shader = ResourceManager.loadShader("assets/shaders/default.glsl")
	shader.compile()

	val texture = ResourceManager.loadTexture("assets/textures/goomba.png")

	while (!window.shouldClose) {
		Time.time = window.time

		Renderer.clear(0.1f, 0.2f, 0.3f, 1.0f)

		Renderer.submit(shader, vao, texture)

		Renderer.swapBuffers(context)
		window.update()
	}

	ResourceManager.clear()

	window.destroy()
}