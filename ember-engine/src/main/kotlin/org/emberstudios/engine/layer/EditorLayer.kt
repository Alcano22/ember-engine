package org.emberstudios.engine.layer

import imgui.ImGui
import imgui.flag.ImGuiDockNodeFlags
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.editor.*
import org.emberstudios.engine.scene.SceneManager
import org.emberstudios.renderer.*

class EditorLayer : Layer {

	enum class RuntimeState {
		EDITOR,
		PLAYING,
		PAUSED
	}

	companion object {
		const val SCENE_FILEPATH = "assets\\scenes\\main.emsc"

		private val LOGGER = getLogger<EditorLayer>()
	}

	private val sceneManager = SceneManager()
	private val editorContext = EditorContext()

	private lateinit var camera: Camera
	private lateinit var framebuffer: Framebuffer

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

		editorContext.registerWindow(ConsoleWindow(), true)
		editorContext.registerWindow(ViewportWindow(framebuffer, this), true)
		editorContext.registerWindow(InspectorWindow(), true)
		editorContext.registerWindow(SceneHierarchyWindow(sceneManager), true)
		editorContext.registerWindow(FileExplorerWindow("assets"), true)
		editorContext.registerWindow(TextEditorWindow(), false)

		initScene()
		editorContext.init()
	}

	fun saveScene() = sceneManager.saveSceneToFile(SCENE_FILEPATH)
	fun reloadScene() = initScene()

	private fun initScene() {
		sceneManager.loadSceneFromFile(SCENE_FILEPATH)
		sceneManager.init()
	}

	override fun onDetach() {
		sceneManager.saveSceneToFile(SCENE_FILEPATH)
	}

	override fun onUpdate(deltaTime: Float) {
		if (runtimeState == RuntimeState.PLAYING)
			sceneManager.update(deltaTime)

		editorContext.update(deltaTime)
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

		editorContext.render()
	}

	override fun onWindowResize(width: Int, height: Int) {
		if (width == 0 || height == 0) return

		framebuffer.resize(width, height)
	}

}