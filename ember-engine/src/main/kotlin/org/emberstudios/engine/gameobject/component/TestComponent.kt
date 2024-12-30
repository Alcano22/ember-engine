package org.emberstudios.engine.gameobject.component

import org.emberstudios.core.logger.getLogger
import org.emberstudios.input.Input
import org.joml.Math
import org.joml.plusAssign
import org.joml.times

class TestComponent : Component() {

	companion object {
		val LOGGER = getLogger<TestComponent>()
	}

	override fun update(deltaTime: Float) {
		transform.position += Input.getAxes() * deltaTime * 2.5f

		var value = transform.scale.x
		value = Math.max(value + Input.mouseScrollDelta.y * .1f, .5f)
		transform.scale.set(value)
	}

}