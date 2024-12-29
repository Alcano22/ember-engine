package org.emberstudios.renderer.opengl

import org.emberstudios.renderer.VertexBuffer
import org.lwjgl.opengl.GL15.*

internal class GLVertexBuffer : VertexBuffer() {

	private val id = glGenBuffers()

	override fun setVertices(vertices: FloatArray) {
		glBindBuffer(GL_ARRAY_BUFFER, id)
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
	}

	override fun bind() {
		glBindBuffer(GL_ARRAY_BUFFER, id)
	}

	override fun unbind() {
		glBindBuffer(GL_ARRAY_BUFFER, 0)
	}

	override fun delete() {
		glDeleteBuffers(id)
	}

}