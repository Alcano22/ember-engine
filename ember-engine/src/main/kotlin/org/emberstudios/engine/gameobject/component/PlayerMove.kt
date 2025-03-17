package org.emberstudios.engine.gameobject.component

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.physics.PhysicsManager
import org.emberstudios.input.Input
import org.emberstudios.input.Key
import org.joml.Vector2f
import org.joml.plus
import org.joml.times
import kotlin.math.sqrt

@Serializable
class PlayerMove : Component() {

	companion object {
		val LOGGER = getLogger<PlayerMove>()
	}

	@ExposeInInspector private var speed = 1f
	@ExposeInInspector private var jumpHeight = 3f

	@Transient private var rb: Rigidbody? = null
	@Transient private var moveX = 0f

	@Transient private var jumpNextFixedUpdate = false

	override fun init() {
		rb = getComponent<Rigidbody>()
	}

	override fun update(deltaTime: Float) {
		moveX = Input.getAxis(Input.Axis.HORIZONTAL)

		if (Input.getKeyDown(Key.SPACE))
			jumpNextFixedUpdate = true
	}

	override fun fixedUpdate(deltaTime: Float) {
		if (rb == null) return

		val move = Vector2f(moveX, 0f) * speed * deltaTime
		rb!!.movePosition(rb!!.position + move)

		if (jumpNextFixedUpdate) {
			rb!!.applyImpulse(Vector2f(0f, getJumpImpulse(jumpHeight)))
			jumpNextFixedUpdate = false
		}
	}

	private fun getJumpImpulse(jumpHeight: Float): Float {
		val g = PhysicsManager.world?.gravity?.y ?: 0f
		val desiredVelocity = sqrt(2f * -g * jumpHeight)
		val mass = rb?.mass ?: 1f
		return mass * desiredVelocity
	}

}