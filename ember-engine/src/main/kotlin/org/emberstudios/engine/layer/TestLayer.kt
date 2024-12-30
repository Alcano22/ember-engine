package org.emberstudios.engine.layer

import org.emberstudios.core.io.ResourceManager
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.Engine
import org.emberstudios.engine.gameobject.GameObject
import org.emberstudios.engine.gameobject.component.SpriteRenderer
import org.emberstudios.engine.gameobject.component.TestComponent
import org.emberstudios.renderer.*

class TestLayer(private val renderContext: RenderContext) : Layer {

	companion object {
		val LOGGER = getLogger<TestLayer>()
	}

	private val gameObjects = mutableListOf<GameObject>()

	private lateinit var camera: Camera

	override fun onAttach() {
		camera = Camera()
		camera.setProjection(16f, 16f / 9f, .1f, 100f)

		Renderer.init(
			RenderAPIType.OPEN_GL,
			renderContext,
			"logs/",
			"logs/",
			camera
		)
		Renderer.viewport(Engine.window.size)

		gameObjects += GameObject("Player").apply {
			addComponent<TestComponent>()
			addComponent<SpriteRenderer> {
				texture = ResourceManager.loadTexture("assets/textures/link.png")
			}
		}

		gameObjects.forEach { it.init() }
	}

	override fun onUpdate(deltaTime: Float) = gameObjects.forEach { it.update(deltaTime) }

	override fun onRender() {
		Renderer.clear(.1f, .2f, .3f, 1f)

		gameObjects.forEach { it.render() }
	}

}