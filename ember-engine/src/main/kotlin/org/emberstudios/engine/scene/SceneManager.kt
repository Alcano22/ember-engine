package org.emberstudios.engine.scene

import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.gameobject.GameObject

class SceneManager {

    companion object {
        val LOGGER = getLogger<SceneManager>()
    }

    var currentScene: Scene? = null
        private set

    fun newScene() { currentScene = Scene() }

    fun init() = executeIfSceneLoaded { init() }
    fun update(deltaTime: Float) = executeIfSceneLoaded { update(deltaTime) }
    fun render() = executeIfSceneLoaded { render() }
    fun clear() = executeIfSceneLoaded { clear() }

    fun loadGameObject(gameObject: GameObject) =
        executeIfSceneLoaded { loadGameObject(gameObject) }
    fun deleteGameObject(gameObject: GameObject) =
        executeIfSceneLoaded { deleteGameObject(gameObject) }

    private fun executeIfSceneLoaded(action: Scene.() -> Unit) =
        currentScene?.action() ?: LOGGER.error { "No scene is loaded!" }

}