package org.emberstudios.engine.scene

import org.emberstudios.core.logger.getLogger
import org.emberstudios.core.utils.GID
import org.emberstudios.engine.gameobject.GameObject
import java.io.File

object SceneManager {

    private val LOGGER = getLogger<SceneManager>()

    var currentScene: Scene? = null
        private set

    fun newScene() { currentScene = Scene() }

    fun loadSceneFromFile(filepath: String) {
        val file = File(filepath)
        if (!file.exists()) {
            LOGGER.error { "Scene file to load from not found: '$filepath'" }
            return
        }
        currentScene = Scene.loadFromFile(file)
    }

    fun saveSceneToFile(filepath: String) {
        val file = File(filepath)
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        currentScene?.saveToFile(file) ?: LOGGER.error { "No scene loaded to save!" }
    }

    fun init() = executeIfSceneLoaded { init() }
    fun update(deltaTime: Float) = executeIfSceneLoaded { update(deltaTime) }
    fun fixedUpdate(deltaTime: Float) = executeIfSceneLoaded { fixedUpdate(deltaTime) }
    fun render() = executeIfSceneLoaded { render() }
    fun clear() = executeIfSceneLoaded { clear() }

    fun findGameObject(gid: GID): GameObject? =
        executeIfSceneLoaded { findGameObject(gid) }
    fun loadGameObject(gameObject: GameObject) =
        executeIfSceneLoaded { loadGameObject(gameObject) }
    fun deleteGameObject(gameObject: GameObject) =
        executeIfSceneLoaded { deleteGameObject(gameObject) }

    private fun <T> executeIfSceneLoaded(action: Scene.() -> T): T? =
        currentScene?.action() ?: run {
            LOGGER.error { "No scene is loaded!" }
            null
        }

}