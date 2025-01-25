package org.emberstudios.engine.gameobject.component

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.emberstudios.engine.gameobject.GameObject

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExposeInInspector

@Serializable
@Polymorphic
sealed class Component {

	@Transient lateinit var gameObject: GameObject
	val transform get() = gameObject.transform

	open fun init() {}
	open fun update(deltaTime: Float) {}
	open fun render() {}
	open fun cleanup() {}
}