package org.emberstudios.core.math

import org.joml.Vector2f
import org.joml.Vector2i

data class BoundsInt(
	var min: Vector2i,
	var max: Vector2i
) {
	constructor(minX: Int, minY: Int, maxX: Int, maxY: Int)
			: this(Vector2i(minX, minY), Vector2i(maxX, maxY))

	var minX: Int
		get() = min.x
		set(value) { min.x = value }
	var minY: Int
		get() = min.y
		set(value) { min.y = value }
	var maxX: Int
		get() = max.x
		set(value) { max.x = value }
	var maxY: Int
		get() = max.y
		set(value) { max.y = value }

	fun contains(vec2i: Vector2i) = vec2i.x >= minX &&
			vec2i.y >= minY &&
			vec2i.x <= maxX &&
			vec2i.y <= maxY

	fun contains(vec2f: Vector2f) = vec2f.x >= minX &&
			vec2f.y >= minY &&
			vec2f.x <= maxX &&
			vec2f.y <= maxY
}

data class Bounds(
	var min: Vector2f,
	var max: Vector2f
) {
	constructor(minX: Float, minY: Float, maxX: Float, maxY: Float)
			: this(Vector2f(minX, minY), Vector2f(maxX, maxY))

	var minX: Float
		get() = min.x
		set(value) { min.x = value }
	var minY: Float
		get() = min.y
		set(value) { min.y = value }
	var maxX: Float
		get() = max.x
		set(value) { max.x = value }
	var maxY: Float
		get() = max.y
		set(value) { max.y = value }

	fun contains(vec2f: Vector2f) = vec2f.x >= minX &&
			vec2f.y >= minY &&
			vec2f.x <= maxX &&
			vec2f.y <= maxY

	fun contains(vec2i: Vector2i) = vec2i.x >= minX &&
			vec2i.y >= minY &&
			vec2i.x <= maxX &&
			vec2i.y <= maxY
}