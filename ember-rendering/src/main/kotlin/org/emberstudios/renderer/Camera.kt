package org.emberstudios.renderer

import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f

/**
 * A simple 2D camera that can be used to render 2D scenes.
 */
class Camera {

	companion object {
		/**
		 * The size of the viewport in pixels.
		 */
		lateinit var viewportSize: Vector2f
	}

	/**
	 * The position of the camera in world space.
	 */
	var position = Vector2f()
		set(value) {
			field.set(value)
			recalculateViewMatrix()
		}

	/**
	 * The rotation of the camera in degrees.
	 */
	var rotation = 0f
		set(value) {
			field = value
			recalculateViewMatrix()
		}

	private var projectionMatrix = Matrix4f()
	private var viewMatrix = Matrix4f()

	/**
	 * The view-projection matrix of the camera.
	 */
	var viewProjectionMatrix = Matrix4f()
		private set

	/**
	 * Sets the projection matrix of the camera to an orthographic projection.
	 *
	 * @param size The size of the camera in world units.
	 * @param aspectRatio The aspect ratio of the camera.
	 * @param zNear The near clipping plane.
	 * @param zFar The far clipping plane.
	 */
	fun setProjection(size: Float, aspectRatio: Float, zNear: Float, zFar: Float) {
		val right = size * aspectRatio * .5f
		val top = size * .5f

		projectionMatrix.ortho(-right, right, -top, top, zNear, zFar)

		recalculateViewMatrix()
	}

	/**
	 * Converts a screen position to a world position.
	 *
	 * @param screenPos The screen position to convert.
	 *
	 * @return The world position.
	 */
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