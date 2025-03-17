package org.emberstudios.core.math

import org.joml.Vector2f
import org.joml.Vector2i

/**
 * A 2D integer bounding box.
 *
 * @param min The minimum point of the bounding box.
 * @param max The maximum point of the bounding box.
 */
data class BoundsInt(
	var min: Vector2i,
	var max: Vector2i
) {
	constructor(minX: Int, minY: Int, maxX: Int, maxY: Int)
			: this(Vector2i(minX, minY), Vector2i(maxX, maxY))

	/**
	 * The minimum x value of the bounding box.
	 */
	var minX: Int
		get() = min.x
		set(value) { min.x = value }

	/**
	 * The minimum y value of the bounding box.
	 */
	var minY: Int
		get() = min.y
		set(value) { min.y = value }

	/**
	 * The maximum x value of the bounding box.
	 */
	var maxX: Int
		get() = max.x
		set(value) { max.x = value }

	/**
	 * The maximum y value of the bounding box.
	 */
	var maxY: Int
		get() = max.y
		set(value) { max.y = value }

	/**
	 * Checks if the given point is within the bounding box.
	 */
	fun contains(vec2i: Vector2i) = vec2i.x >= minX &&
			vec2i.y >= minY &&
			vec2i.x <= maxX &&
			vec2i.y <= maxY

	/**
	 * Checks if the given point is within the bounding box.
	 */
	fun contains(vec2f: Vector2f) = vec2f.x >= minX &&
			vec2f.y >= minY &&
			vec2f.x <= maxX &&
			vec2f.y <= maxY
}

/**
 * A 2D floating-point bounding box.
 *
 * @param min The minimum point of the bounding box.
 * @param max The maximum point of the bounding box.
 */
data class Bounds(
	var min: Vector2f,
	var max: Vector2f
) {
	constructor(minX: Float, minY: Float, maxX: Float, maxY: Float)
			: this(Vector2f(minX, minY), Vector2f(maxX, maxY))

	/**
	 * The minimum x value of the bounding box.
	 */
	var minX: Float
		get() = min.x
		set(value) { min.x = value }

	/**
	 * The minimum y value of the bounding box.
	 */
	var minY: Float
		get() = min.y
		set(value) { min.y = value }

	/**
	 * The maximum x value of the bounding box.
	 */
	var maxX: Float
		get() = max.x
		set(value) { max.x = value }

	/**
	 * The maximum y value of the bounding box.
	 */
	var maxY: Float
		get() = max.y
		set(value) { max.y = value }

	/**
	 * Checks if the given point is within the bounding box.
	 */
	fun contains(vec2f: Vector2f) = vec2f.x >= minX &&
			vec2f.y >= minY &&
			vec2f.x <= maxX &&
			vec2f.y <= maxY

	/**
	 * Checks if the given point is within the bounding box.
	 */
	fun contains(vec2i: Vector2i) = vec2i.x >= minX &&
			vec2i.y >= minY &&
			vec2i.x <= maxX &&
			vec2i.y <= maxY
}