package org.emberstudios.renderer

class BatchRenderer {

	private val vertices = mutableListOf<Float>()
	private val indices = mutableListOf<Int>()

	private var vertexCount = 0;

	fun begin() {
		vertices.clear()
		indices.clear()
		vertexCount = 0
	}

	fun submit(vertexData: FloatArray, indexData: IntArray) {
		vertices += vertexData.toList()
		indices += indexData.map { it + vertexCount }
		vertexCount += vertexData.size / 4
	}

	fun end() {
		val vertexBuffer = VertexBuffer.create(vertices.toFloatArray())
		vertexBuffer.layout = bufferLayout {
			float2("a_Position")
			float2("a_TexCoord")
		}

		val indexBuffer = IndexBuffer.create(indices.toIntArray())

		val vertexArray = VertexArray.create()
		vertexArray.addVertexBuffer(vertexBuffer)
		vertexArray.setIndexBuffer(indexBuffer)

		vertexArray.bind()
		Renderer.drawIndexed(vertexArray)
		vertexArray.unbind()

		vertexBuffer.delete()
		indexBuffer.delete()
	}

}