package org.emberstudios.renderer

/**
 * Represents the data type of shader attribute.
 *
 * @param componentCount The number of components in the data type.
 * @param singleSize The size of a single component in bytes.
 */
enum class ShaderDataType(val componentCount: Int, private val singleSize: Int) {

	BOOL(1, 1),
	INT(1, Int.SIZE_BYTES),
	INT2(2, Int.SIZE_BYTES),
	INT3(3, Int.SIZE_BYTES),
	INT4(4, Int.SIZE_BYTES),
	FLOAT(1, Float.SIZE_BYTES),
	FLOAT2(2, Float.SIZE_BYTES),
	FLOAT3(3, Float.SIZE_BYTES),
	FLOAT4(4, Float.SIZE_BYTES),
	MAT3(3 * 3, Float.SIZE_BYTES),
	MAT4(4 * 4, Float.SIZE_BYTES);

	/**
	 * The size of the data type in bytes.
	 */
	val size = componentCount * singleSize

}