package org.emberstudios.engine.scene

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.emberstudios.core.utils.GID
import org.emberstudios.engine.gameobject.GameObject
import org.emberstudios.engine.serialization.jsonFormat
import java.io.File

@Serializable
class Scene {

    companion object {
        fun loadFromFile(file: File) = jsonFormat.decodeFromString(serializer(), file.readText())
    }

    private val _gameObjects = mutableListOf<GameObject>()

    val gameObjects get() = _gameObjects.toList()

    fun init() = _gameObjects.forEach { it.init() }
    fun update(deltaTime: Float) = _gameObjects.forEach { it.update(deltaTime) }
    fun fixedUpdate(deltaTime: Float) = _gameObjects.forEach { it.fixedUpdate(deltaTime) }
    fun render() = _gameObjects.forEach { it.render() }
    fun clear() = _gameObjects.clear()

    fun findGameObject(gid: GID) = _gameObjects.first { it.gid == gid }

    fun loadGameObject(gameObject: GameObject): GameObject {
        _gameObjects += gameObject
        gameObject.init()
        return gameObject
    }

    fun deleteGameObject(gameObject: GameObject) {
        _gameObjects -= gameObject
        gameObject.cleanup()
    }

    fun saveToFile(file: File) = file.writeText(jsonFormat.encodeToString(serializer(), this))

}