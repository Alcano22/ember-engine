package org.emberstudios.renderer.opengl

import org.emberstudios.core.logger.getLogger
import org.emberstudios.renderer.IndexBuffer
import org.emberstudios.renderer.ShaderDataType
import org.emberstudios.renderer.VertexArray
import org.emberstudios.renderer.VertexBuffer
import org.lwjgl.opengl.GL30.*

internal class GLVertexArray : VertexArray {

	companion object {
		private val LOGGER = getLogger<GLVertexBuffer>()

		private fun shaderDataTypeToOpenGLBaseType(type: ShaderDataType): Int = when (type) {
			ShaderDataType.BOOL -> GL_BOOL
			ShaderDataType.INT -> GL_INT
			ShaderDataType.INT2 -> GL_INT
			ShaderDataType.INT3 -> GL_INT
			ShaderDataType.INT4 -> GL_INT
			ShaderDataType.FLOAT -> GL_FLOAT
			ShaderDataType.FLOAT2 -> GL_FLOAT
			ShaderDataType.FLOAT3 -> GL_FLOAT
			ShaderDataType.FLOAT4 -> GL_FLOAT
			ShaderDataType.MAT3 -> GL_FLOAT
			ShaderDataType.MAT4 -> GL_FLOAT
		}
	}

	private val id = glGenVertexArrays()

	private val vertexBuffers = mutableListOf<VertexBuffer>()
	private var indexBuffer: IndexBuffer? = null

	init {
		glBindVertexArray(id)
	}

	override fun addVertexBuffer(vertexBuffer: VertexBuffer) {
		bind()
		vertexBuffer.bind()

		val layout = vertexBuffer.layout
		if (layout.elements.isEmpty()) {
			LOGGER.error { "VertexBuffer has no layout!" }
			return
		}

		for ((index, element) in layout.withIndex()) {
			glEnableVertexAttribArray(index)
			glVertexAttribPointer(
				index,
				element.componentCount,
				shaderDataTypeToOpenGLBaseType(element.type),
				element.normalized,
				layout.stride,
				element.offset.toLong()
			)
		}

		unbind()
	}

	override fun getIndexBuffer(): IndexBuffer? = indexBuffer

	override fun setIndexBuffer(indexBuffer: IndexBuffer) {
		bind()

		indexBuffer.bind()
		this.indexBuffer = indexBuffer

		unbind()
	}

	override fun bind() {
		glBindVertexArray(id)
	}

	override fun unbind() {
		glBindVertexArray(0)
	}

}