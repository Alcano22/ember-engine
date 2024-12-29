package org.emberstudios.engine

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.engine.util.Time
import org.emberstudios.renderer.*
import org.emberstudios.window.WindowType

val LOGGER = KotlinLogging.logger("ENGINE")

fun main() {
	val window = WindowType.GLFW.create()

	if (!window.init("Ember Engine", 800, 600))
		return

	window.setResizeCallback { width, height -> Renderer.viewport(width, height)}

	val context = window.createRenderContext()

	Renderer.init(RenderAPIType.OPEN_GL, context, "logs/", "logs/")
	Renderer.viewport(window.size)

	val vertices = floatArrayOf(
		// Position         Color
		-0.5f,  0.5f,       1.0f, 0.0f, 0.0f,
		 0.5f,  0.5f,       0.0f, 1.0f, 0.0f,
		-0.5f, -0.5f,       0.0f, 0.0f, 1.0f,
		 0.5f, -0.5f,       1.0f, 0.0f, 1.0f
	)
	val indices = intArrayOf(
		0, 1, 2,
		1, 3, 2
	)

	val vao = VertexArray.create()
	val vbo = VertexBuffer.create()
	val ebo = IndexBuffer.create(indices)

	vbo.setVertices(vertices)
	vbo.layout = BufferLayout(
		BufferElement("a_Position", ShaderDataType.FLOAT2),
		BufferElement("a_Color", ShaderDataType.FLOAT3)
	)

	vao.addVertexBuffer(vbo)
	vao.setIndexBuffer(ebo)

	val vertexSrc = """
		#version 330 core
		layout(location = 0) in vec2 a_Position;
		layout(location = 1) in vec3 a_Color;
		out vec3 f_Color;
		void main()
		{
			f_Color = a_Color;
		
			gl_Position = vec4(a_Position, 0.0, 1.0);
		}
	""".trimIndent()

	val fragmentSrc = """
		#version 330 core
		in vec3 f_Color;
		out vec4 FragColor;
		void main()
		{
			FragColor = vec4(f_Color, 1.0);
		}
	""".trimIndent()

	val shader = Shader.create(vertexSrc, fragmentSrc)
	shader.compile()

	while (!window.shouldClose) {
		Time.time = window.time

		Renderer.clear(0.1f, 0.2f, 0.3f, 1.0f)

		Renderer.submit(shader, vao)

		Renderer.swapBuffers(context)
		window.update()
	}

	window.destroy()
}