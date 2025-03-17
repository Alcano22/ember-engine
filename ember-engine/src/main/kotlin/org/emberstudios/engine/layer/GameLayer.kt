package org.emberstudios.engine.layer

import org.emberstudios.engine.Engine
import org.emberstudios.engine.layer.EditorLayer.Companion.SCENE_FILEPATH
import org.emberstudios.engine.networking.NetworkingManager
import org.emberstudios.engine.scene.SceneManager
import org.emberstudios.renderer.Camera
import org.emberstudios.renderer.Renderer

class GameLayer : Layer {

	private val gameRuntime get() = Engine.gameRuntime

	private lateinit var camera: Camera

	override fun onAttach() {
		camera = Camera()
		camera.setProjection(16f, 16f / 9f, .1f, 100f)

		loadScene()
	}

	private fun loadScene() {
		SceneManager.loadSceneFromFile(SCENE_FILEPATH)
		SceneManager.init()
	}

	override fun onDetach() {
		gameRuntime.stop()
		NetworkingManager.cleanup()
	}

	override fun onUpdate(deltaTime: Float) {
		Engine.gameRuntime.update(deltaTime)
	}

	override fun onRender() {
		Renderer.clear(.1f, .2f, .3f, 1f)

		Renderer.beginScene(camera)
		SceneManager.render()
		Renderer.endScene()
	}

	override fun onWindowResize(width: Int, height: Int) {
		if (width == 0 || height == 0) return

//		camera.setProjection(16f, width.toFloat() / height.toFloat(), .1f, 100f)
		Renderer.viewport(width, height)
	}

}