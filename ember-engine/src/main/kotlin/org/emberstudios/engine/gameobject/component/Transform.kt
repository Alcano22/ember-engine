package org.emberstudios.engine.gameobject.component

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f

@Serializable
class Transform : Component() {
	@Contextual var position = Vector2f()
	var rotation = 0f
	@Contextual var scale = Vector2f(1f, 1f)

	fun toMatrix(): Matrix4f = Matrix4f()
		.translate(Vector3f(position, 0f))
		.rotateZ(rotation)
		.scale(Vector3f(scale, 1f))
}