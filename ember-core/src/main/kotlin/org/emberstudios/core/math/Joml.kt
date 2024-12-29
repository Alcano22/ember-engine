package org.emberstudios.core.math

import org.joml.*

fun Vector2i.toArray() = intArrayOf(x, y)
fun Vector3i.toArray() = intArrayOf(x, y, z)
fun Vector4i.toArray() = intArrayOf(x, y, z, w)

fun Vector2f.toArray() = floatArrayOf(x, y)
fun Vector3f.toArray() = floatArrayOf(x, y, z)
fun Vector4f.toArray() = floatArrayOf(x, y, z, w)

// Float Vectors
operator fun Vector2f.plus(other: Vector2f): Vector2f = Vector2f(this).add(other)
operator fun Vector2f.minus(other: Vector2f): Vector2f = Vector2f(this).sub(other)
operator fun Vector2f.times(scalar: Float): Vector2f = Vector2f(this).mul(scalar)
operator fun Vector2f.times(scalar: Int): Vector2f = Vector2f(this).mul(scalar.toFloat())
operator fun Vector2f.unaryMinus(): Vector2f = Vector2f(this).negate()

operator fun Vector3f.plus(other: Vector3f): Vector3f = Vector3f(this).add(other)
operator fun Vector3f.minus(other: Vector3f): Vector3f = Vector3f(this).sub(other)
operator fun Vector3f.times(scalar: Float): Vector3f = Vector3f(this).mul(scalar)
operator fun Vector3f.times(scalar: Int): Vector3f = Vector3f(this).mul(scalar.toFloat())
operator fun Vector3f.unaryMinus(): Vector3f = Vector3f(this).negate()

operator fun Vector4f.plus(other: Vector4f): Vector4f = Vector4f(this).add(other)
operator fun Vector4f.minus(other: Vector4f): Vector4f = Vector4f(this).sub(other)
operator fun Vector4f.times(scalar: Float): Vector4f = Vector4f(this).mul(scalar)
operator fun Vector4f.times(scalar: Int): Vector4f = Vector4f(this).mul(scalar.toFloat())
operator fun Vector4f.unaryMinus(): Vector4f = Vector4f(this).negate()

// Integer Vectors
operator fun Vector2i.plus(other: Vector2i): Vector2i = Vector2i(this).add(other)
operator fun Vector2i.minus(other: Vector2i): Vector2i = Vector2i(this).sub(other)
operator fun Vector2i.times(scalar: Int): Vector2i = Vector2i(this).mul(scalar)
operator fun Vector2i.unaryMinus(): Vector2i = Vector2i(this).negate()

operator fun Vector3i.plus(other: Vector3i): Vector3i = Vector3i(this).add(other)
operator fun Vector3i.minus(other: Vector3i): Vector3i = Vector3i(this).sub(other)
operator fun Vector3i.times(scalar: Int): Vector3i = Vector3i(this).mul(scalar)
operator fun Vector3i.unaryMinus(): Vector3i = Vector3i(this).negate()

operator fun Vector4i.plus(other: Vector4i): Vector4i = Vector4i(this).add(other)
operator fun Vector4i.minus(other: Vector4i): Vector4i = Vector4i(this).sub(other)
operator fun Vector4i.times(scalar: Int): Vector4i = Vector4i(this).mul(scalar)
operator fun Vector4i.unaryMinus(): Vector4i = Vector4i(this).negate()

// Matrices
operator fun Matrix3f.times(other: Matrix3f): Matrix3f = Matrix3f(this).mul(other)
operator fun Matrix3f.times(vector: Vector3f): Vector3f = Vector3f(vector).mul(this)

operator fun Matrix4f.times(other: Matrix4f): Matrix4f = Matrix4f(this).mul(other)
operator fun Matrix4f.times(vector: Vector4f): Vector4f = Vector4f(vector).mul(this)

// Quaternions
operator fun Quaternionf.plus(other: Quaternionf): Quaternionf = Quaternionf(this).add(other)
operator fun Quaternionf.times(other: Quaternionf): Quaternionf = Quaternionf(this).mul(other)
operator fun Quaternionf.times(vector: Vector3f): Vector3f {
    val result = Vector3f()
    this.transform(vector, result)
    return result
}
operator fun Quaternionf.unaryMinus(): Quaternionf = Quaternionf(this).conjugate()