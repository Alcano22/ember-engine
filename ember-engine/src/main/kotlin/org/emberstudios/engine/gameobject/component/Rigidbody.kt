package org.emberstudios.engine.gameobject.component

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.emberstudios.engine.physics.PhysicsManager
import org.emberstudios.engine.physics.PhysicsUserData
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.FixtureDef
import org.joml.Vector2f

@Serializable
class Rigidbody(
	val mass: Float = 1f,
	val friction: Float = .3f,
	val restitution: Float = .2f,
	var type: BodyType = BodyType.STATIC,
	var continuous: Boolean = false
) : Component() {

	@Transient var body: Body? = null
		private set

	val position get() = body?.position?.let { Vector2f(it.x, it.y) } ?: Vector2f()
	val velocity get() = body?.linearVelocity?.let { Vector2f(it.x, it.y) } ?: Vector2f()

	override fun init() {
		PhysicsManager.addCleanupCallback { body = null }

		val bodyDef = BodyDef().apply {
			this@apply.type = this@Rigidbody.type
			bullet = continuous
			position.set(transform.position.x, transform.position.y)
			angle = transform.rotation
		}

		PhysicsManager.world?.let { world ->
			body = world.createBody(bodyDef)

			val shape = PolygonShape().apply {
				setAsBox(transform.scale.x * .5f, transform.scale.y * .5f)
			}

			val fixtureDef = FixtureDef().apply {
				this.shape = shape
				density = mass
				friction = this@Rigidbody.friction
				restitution = this@Rigidbody.restitution
				userData = PhysicsUserData(gameObject.name)
			}
			body?.createFixture(fixtureDef)
		}
	}

	override fun fixedUpdate(deltaTime: Float) {
		body?.let {
			transform.position.x = it.position.x
			transform.position.y = it.position.y
			transform.rotation = it.angle
		}
	}

	fun movePosition(position: Vector2f) {
		body?.setTransform(Vec2(position.x, position.y), body?.angle ?: 0f)
		gameObject.transform.position.set(position.x, position.y)
	}

	fun applyForce(force: Vector2f) = body?.applyForceToCenter(Vec2(force.x, force.y))
	fun applyImpulse(impulse: Vector2f) = body?.applyLinearImpulse(Vec2(impulse.x, impulse.y), body!!.worldCenter)

}