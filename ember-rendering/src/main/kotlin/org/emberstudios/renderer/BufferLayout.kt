package org.emberstudios.renderer

data class BufferElement(
	val name: String,
	val type: ShaderDataType,
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