package org.emberstudios.engine.gameobject.component

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.emberstudios.core.utils.GID
import org.emberstudios.engine.gameobject.GameObject

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExposeInInspector

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class ReadOnlyInInspector

@Serializable
@Polymorphic
sealed class Component {

	@Transient lateinit var gameObject: GameObject
	val transform get() = gameObject.transform
	var gid = GID()

	open fun init() {}
	open fun update(deltaTime: Float) {}
	open fun fixedUpdate(deltaTime: Float) {}
	open fun render() {}
	open fun cleanup() {}

	protected inline fun <reified T : Component> getComponents() = gameObject.getComponents<T>()
	protected inline fun <reified T : Component> getComponent() = gameObject.getComponent<T>()

	protected inline fun <reified T : Component> removeComponents() = gameObject.removeComponents<T>()
	protected inline fun <reified T : Component> removeComponent() = gameObject.removeComponent<T>()
}