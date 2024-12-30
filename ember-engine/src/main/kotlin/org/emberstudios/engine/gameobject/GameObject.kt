package org.emberstudios.engine.gameobject

import org.emberstudios.engine.gameobject.component.Component
import org.emberstudios.engine.gameobject.component.Transform
import org.joml.Vector2f
import kotlin.reflect.full.createInstance

class GameObject(
	val name: String = "GameObject",
	position: Vector2f = Vector2f(),
	rotation: Float = 0f,
	scale: Vector2f = Vector2f(1f, 1f)
) {

	val components = mutableListOf<Component>()

	val transform get() = components[0] as Transform

	init {
		components += Transform().apply {
			this.position = position
			this.rotation = rotation
			this.scale = scale
		}
	}

	fun init() = components.forEach { it.init() }

	fun update(deltaTime: Float) = components.forEach {
		it.gameObject = this
		it.update(deltaTime)
	}

	fun render() = components.forEach { it.render() }

	inline fun <reified T : Component> addComponent(configure: T.() -> Unit = {}): T {
		val component = T::class.createInstance()
		component.configure()
		components += component
		return component
	}

	inline fun <reified T : Component> getComponents() = components.filterIsInstance<T>().toTypedArray()
	inline fun <reified T : Component> getComponent() = getComponents<T>().firstOrNull()

	inline fun <reified T : Component> removeComponents() = components.removeIf { it is T }
	inline fun <reified T : Component> removeComponent() = components.removeAt(components.indexOfFirst { it is T })

}