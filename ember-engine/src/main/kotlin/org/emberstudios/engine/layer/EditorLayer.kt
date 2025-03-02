package org.emberstudios.engine.layer

import imgui.ImGui
import imgui.ImVec2
import imgui.flag.ImGuiDockNodeFlags
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.Engine
import org.emberstudios.engine.editor.*
import org.emberstudios.engine.networking.NetworkingManager
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

	private val editorContext = EditorContext()

	private val sceneManager = SceneManager()
	private val networkingManager = NetworkingManager()

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
		editorContext.registerWindow(ServerConnectWindow(networkingManager), false)
		editorContext.registerWindow(ServerConnectWindow.SuccessWindow(), false)
		editorContext.registerWindow(ServerInfoWindow(networkingManager), false)
		editorContext.registerWindow(ControllerTestWindow(), false)

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
		networkingManager.cleanup()

		sceneManager.saveSceneToFile(SCENE_FILEPATH)
		editorContext.saveConfig()
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

		if (ImGui.beginMainMenuBar()) {
			val connectedToServer = networkingManager.isConnected

			if (ImGui.beginMenu("File")) {
				if (ImGui.menuItem("Quit"))
					Engine.quit()

				ImGui.endMenu()
			}

			if (ImGui.beginMenu("Edit")) {


				ImGui.endMenu()
			}

			if (ImGui.beginMenu("View")) {
				if (ImGui.beginMenu("Windows")) {
					if (ImGui.menuItem("Server Info"))
						editorContext.show<ServerInfoWindow>()

					ImGui.endMenu()
				}

				if (ImGui.beginMenu("Input")) {
					if (ImGui.menuItem("Controller Test"))
						editorContext.show<ControllerTestWindow>()

					ImGui.endMenu()
				}

				ImGui.endMenu()
			}

			if (ImGui.beginMenu("Networking")) {
				if (connectedToServer) ImGui.beginDisabled()
				if (ImGui.menuItem("Connect to Server"))
					editorContext.showCentered<ServerConnectWindow>(ImVec2(300f, 200f))
				if (connectedToServer) ImGui.endDisabled()

				if (!connectedToServer) ImGui.beginDisabled()
				if (ImGui.menuItem("Disconnect from Server"))
					networkingManager.disconnect()
				if (!connectedToServer) ImGui.endDisabled()

				ImGui.endMenu()
			}

			ImGui.endMainMenuBar()
		}

		editorContext.render()
	}

	override fun onWindowResize(width: Int, height: Int) {
		if (width == 0 || height == 0) return

		framebuffer.resize(width, height)
	}

}