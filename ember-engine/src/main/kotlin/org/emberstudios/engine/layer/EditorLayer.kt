package org.emberstudios.engine.layer

import imgui.ImGui
import imgui.flag.ImGuiDockNodeFlags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.editor.*
import org.emberstudios.engine.scene.SceneManager
import org.emberstudios.networking.GameClient
import org.emberstudios.networking.NetTransform
import org.emberstudios.renderer.*
import java.util.*

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
	private val gameClient = GameClient()

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

		CoroutineScope(Dispatchers.IO).launch {
			gameClient.connect()
			LOGGER.debug { "Test" }
			gameClient.sendMessage("Hello from client!")

			val netTransform = NetTransform(
				UUID.randomUUID().toString(),
				1f, 2f, 3f,
				0f,
				1f, 3f, 5f
			)
			gameClient.sendTransform(netTransform)
		}
	}

	fun saveScene() = sceneManager.saveSceneToFile(SCENE_FILEPATH)
	fun reloadScene() = initScene()

	private fun initScene() {
		sceneManager.loadSceneFromFile(SCENE_FILEPATH)
		sceneManager.init()
	}

	override fun onDetach() {
		sceneManager.saveSceneToFile(SCENE_FILEPATH)
		editorContext.saveConfig()

		CoroutineScope(Dispatchers.IO).launch {
			gameClient.disconnect()
		}
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