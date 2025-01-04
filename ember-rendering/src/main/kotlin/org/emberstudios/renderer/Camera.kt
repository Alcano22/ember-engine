package org.emberstudios.renderer

import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f

class Camera {

	companion object {
		lateinit var viewportSize: Vector2f
	}

	var position = Vector2f()
		set(value) {
			field.set(value)
			recalculateViewMatrix()
		}

	var rotation = 0f
		set(value) {
			field = value
			recalculateViewMatrix()
		}

	private var projectionMatrix = Matrix4f()
	private var viewMatrix = Matrix4f()

	var viewProjectionMatrix = Matrix4f()
		private set

	fun setProjection(size: Float, aspectRatio: Float, zNear: Float, zFar: Float) {
		val right = size * aspectRatio * .5f
		val top = size * .5f

		projectionMatrix.ortho(-right, right, -top, top, zNear, zFar)

		recalculateViewMatrix()
	}

	fun screenToWorldPoint(screenPos: Vector2f): Vector2f {
		val ndcX = (2f * screenPos.x) / viewportSize.x - 1f
		val ndcY = 1f - (2f * screenPos.y) / viewportSize.y

		val ndc = Vector4f(ndcX, ndcY, 0f, 1f)

		val invViewProj = Matrix4f(viewProjectionMatrix).invert()
		val worldPos = invViewProj.transform(ndc)

		return Vector2f(worldPos.x / worldPos.w, worldPos.y / worldPos.w)
	}

	private fun recalculateViewMatrix() {
		val transform = Matrix4f()
			.translate(Vector3f(position, 10f))
			.rotateZ(Math.toRadians(rotation))

		viewMatrix = transform.invert(Matrix4f())

		viewProjectionMatrix = Matrix4f(projectionMatrix).mul(viewMatrix)
	}

}