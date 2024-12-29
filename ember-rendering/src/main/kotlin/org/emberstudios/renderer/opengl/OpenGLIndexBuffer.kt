package org.emberstudios.renderer.opengl

import org.emberstudios.renderer.IndexBuffer
import org.lwjgl.opengl.GL15.*

internal class OpenGLIndexBuffer(indices: IntArray) : IndexBuffer {

	private val id = glGenBuffers()

	override val count = indices.size

	init {
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id)
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)
	}

	override fun bind() {
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id)
	}

	override fun unbind() {
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
	}

}