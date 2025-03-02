package org.emberstudios.engine.gameobject.component

import kotlinx.serialization.Serializable
import org.emberstudios.core.logger.getLogger
import org.emberstudios.input.ControllerButton
import org.emberstudios.input.Input
import org.joml.plusAssign
import org.joml.times

@Serializable
class PlayerMove : Component() {

	companion object {
		val LOGGER = getLogger<PlayerMove>()
	}

	@ExposeInInspector private var speed = 1f

	override fun update(deltaTime: Float) {
		if (Input.getControllerButtonDown(ControllerButton.A))
			Input.setControllerVibration(1f, 1f)
		if (Input.getControllerButtonUp(ControllerButton.A))
			Input.setControllerVibration(0f, 0f)

		val moveInput = Input.getAxes()
		if (moveInput.x == 0f && moveInput.y == 0f) return

		transform.position += moveInput.normalize() * speed * deltaTime
	}

}