package org.emberstudios.networking

import kotlinx.serialization.Serializable

@Serializable
data class NetTransform(
	val gameObjectId: String,
	val x: Float,
	val y: Float,
	val z: Float,
	val rotation: Float,
	val scaleX: Float,
	val scaleY: Float,
	val scaleZ: Float
)