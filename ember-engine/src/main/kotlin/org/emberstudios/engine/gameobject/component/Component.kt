package org.emberstudios.engine.gameobject.component

import org.emberstudios.engine.gameobject.GameObject

abstract class Component {

	lateinit var gameObject: GameObject
	val transform get() = gameObject.transform

	open fun init() {}
	open fun update(deltaTime: Float) {}
	open fun render() {}

}