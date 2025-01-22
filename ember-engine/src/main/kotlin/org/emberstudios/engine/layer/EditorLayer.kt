package org.emberstudios.engine.layer

import imgui.ImGui
import imgui.flag.ImGuiDockNodeFlags
import org.emberstudios.core.io.ResourceManager
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.editor.EditorWindow
import org.emberstudios.engine.editor.HierarchyWindow
import org.emberstudios.engine.editor.InspectorWindow
import org.emberstudios.engine.editor.ViewportWindow
import org.emberstudios.engine.gameobject.GameObject
import org.emberstudios.engine.gameobject.component.SpriteRenderer
import org.emberstudios.engine.gameobject.component.TestComponent
import org.emberstudios.engine.scene.SceneManager
import org.emberstudios.renderer.*

class EditorLayer : Layer {

	enum class RuntimeState {
		EDITOR,
		PLAYING,
		PAUSED
	}

	companion object {
		private val LOGGER = getLogger<EditorLayer>()
	}

	private val sceneManager = SceneManager()
	private val editorWindows = mutableListOf<EditorWindow>()

	private lateinit var camera: Camera
	private lateinit var framebuffer: Framebuffer

	private lateinit var spritesheet: SpriteSheet

	var runtimeState = RuntimeState.EDITOR
		set(value) {
			val initScene = field == RuntimeState.PLAYING && value == RuntimeState.EDITOR
			field = value
			if (initScene) initScene()
		}

	override fun onAttach() {
		camera = Camera()
		camera.setProjection(16f, 16f / 9f, .1f, 100f)

		framebuffer = Framebuffer.create(Framebuffer.Specs(1, 1))

		editorWindows += ViewportWindow(framebuffer, this, true)
		val inspectorWindow = InspectorWindow(camera, true)
		editorWindows += inspectorWindow
		editorWindows += HierarchyWindow(inspectorWindow, sceneManager, true)

		spritesheet = SpriteSheet("assets/textures/link.png", 3, 3)

		initScene()
		editorWindows.forEach { it.init() }
	}

	private fun initScene() {
		sceneManager.newScene()
		sceneManager.loadGameObject(GameObject("Player").apply {
			addComponent<TestComponent>()
			addComponent<SpriteRenderer> {
				texture = spritesheet[4, 2]
			}
		})

		sceneManager.init()
	}

	override fun onUpdate(deltaTime: Float) {
		if (runtimeState == RuntimeState.PLAYING)
			sceneManager.update(deltaTime)

		editorWindows.forEach { it.update(deltaTime) }
	}

	override fun onRender() {
		framebuffer.bind()

		Renderer.clear(.1f, .2f, .3f, 1f)

		Renderer.beginScene(camera)
		sceneManager.render()
		Renderer.endScene()

		framebuffer.unbind()
	}

	override fun onRenderImGui() {
		ImGui.dockSpaceOverViewport(ImGui.getMainViewport(), ImGuiDockNodeFlags.PassthruCentralNode)

		editorWindows.forEach { it.render() }
	}

	override fun onWindowResize(width: Int, height: Int) {
		if (width == 0 || height == 0) return

		framebuffer.resize(width, height)
	}

}