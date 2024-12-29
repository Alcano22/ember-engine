package org.emberstudios.renderer

data class BufferElement(
	val type: ShaderDataType,
	val name: String,
	val normalized: Boolean = false
) {
	val size = type.size
	val componentCount = type.componentCount
	var offset = 0
}

class BufferLayout(vararg val elements: BufferElement) : Iterable<BufferElement> {

	var stride = 0
		private set

	init {
		var offset = 0
		stride = 0
		for (element in elements) {
			element.offset = offset
			offset += element.size
			stride += element.size
		}
	}

	override fun iterator() = elements.iterator()
}

fun bufferLayout(block: BufferLayoutBuilder.() -> Unit): BufferLayout {
	val builder = BufferLayoutBuilder()
	builder.block()
	return builder.build()
}

class BufferLayoutBuilder {
	private val elements = mutableListOf<BufferElement>()

	fun element(type: ShaderDataType, name: String, normalized: Boolean = false) {
		elements += BufferElement(type, name, normalized)
	}

	fun build() = BufferLayout(*elements.toTypedArray())
}
