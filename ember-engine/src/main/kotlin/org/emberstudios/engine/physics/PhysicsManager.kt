package org.emberstudios.engine.physics

import org.emberstudios.core.logger.getLogger
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.*

object PhysicsManager {

	private const val VELOCITY_ITERATIONS = 8
	private const val POSITION_ITERATIONS = 3

	private val LOGGER = getLogger<PhysicsManager>()

	private val cleanupCallbacks = mutableListOf<() -> Unit>()

	var world: World? = null
		private set

	fun addCleanupCallback(callback: () -> Unit) { cleanupCallbacks += callback }

	fun init() {
		world = World(Vec2(0f, -9.81f))
		world?.setContactListener(CollisionListener())
	}

	fun update(deltaTime: Float) {
		world?.step(deltaTime, VELOCITY_ITERATIONS, POSITION_ITERATIONS)
	}

	fun cleanup() {
		cleanupCallbacks.forEach { it() }
		world = null
	}

}