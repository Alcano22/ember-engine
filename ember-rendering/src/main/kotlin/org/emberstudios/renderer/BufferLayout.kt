package org.emberstudios.renderer

/**
 * Represents the data type of shader attribute.
 */
data class BufferElement(
	val type: ShaderDataType,
	val name: String,
	val normalized: Boolean = false
) {
	val size = type.size
	val componentCount = type.componentCount
	var offset = 0
}

/**
 * Represents the layout of a buffer.
 *
 * @property elements The elements of the buffer layout.
 */
class BufferLayout(vararg val elements: BufferElement) : Iterable<BufferElement> {

	/**
	 * The stride of the buffer layout.
	 */
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

/**
 * Creates a [BufferLayout].
 *
 * @param block The block to configure the buffer layout.
 *
 * @return The created buffer layout.
 */
fun bufferLayout(block: BufferLayoutBuilder.() -> Unit): BufferLayout {
	val builder = BufferLayoutBuilder()
	builder.block()
	return builder.build()
}

/**
 * Builder for [BufferLayout].
 */
class BufferLayoutBuilder {
	private val elements = mutableListOf<BufferElement>()

	fun element(type: ShaderDataType, name: String, normalized: Boolean = false) {
		elements += BufferElement(type, name, normalized)
	}

	fun bool(name: String, normalized: Boolean = false) =
		element(ShaderDataType.BOOL, name, normalized)
	fun int(name: String, normalized: Boolean = false) =
		element(ShaderDataType.INT, name, normalized)
	fun int2(name: String, normalized: Boolean = false) =
		element(ShaderDataType.INT2, name, normalized)
	fun int3(name: String, normalized: Boolean = false) =
		element(ShaderDataType.INT3, name, normalized)
	fun int4(name: String, normalized: Boolean = false) =
		element(ShaderDataType.INT4, name, normalized)
	fun float(name: String, normalized: Boolean = false) =
		element(ShaderDataType.FLOAT, name, normalized)
	fun float2(name: String, normalized: Boolean = false) =
		element(ShaderDataType.FLOAT2, name, normalized)
	fun float3(name: String, normalized: Boolean = false) =
		element(ShaderDataType.FLOAT3, name, normalized)
	fun float4(name: String, normalized: Boolean = false) =
		element(ShaderDataType.FLOAT4, name, normalized)
	fun mat3(name: String, normalized: Boolean = false) =
		element(ShaderDataType.MAT3, name, normalized)
	fun mat4(name: String, normalized: Boolean = false) =
		element(ShaderDataType.MAT4, name, normalized)

	fun build() = BufferLayout(*elements.toTypedArray())
}
