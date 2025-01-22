package org.emberstudios.engine.scene

import org.emberstudios.engine.gameobject.GameObject

class Scene {

    private val _gameObjects = mutableListOf<GameObject>()

    val gameObjects get() = _gameObjects.toList()

    fun init() = _gameObjects.forEach { it.init() }
    fun update(deltaTime: Float) = _gameObjects.forEach { it.update(deltaTime) }
    fun render() = _gameObjects.forEach { it.render() }
    fun clear() = _gameObjects.clear()

    fun loadGameObject(gameObject: GameObject): GameObject {
        _gameObjects += gameObject
        gameObject.init()
        return gameObject
    }

    fun deleteGameObject(gameObject: GameObject) {
        _gameObjects -= gameObject
        gameObject.cleanup()
    }

}