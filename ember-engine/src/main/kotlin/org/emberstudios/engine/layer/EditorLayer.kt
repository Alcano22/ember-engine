package org.emberstudios.engine.layer

import imgui.ImGui
import imgui.ImVec2
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiDockNodeFlags
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.Engine
import org.emberstudios.engine.editor.*
import org.emberstudios.engine.networking.NetworkingManager
import org.emberstudios.engine.scene.SceneManager
import org.emberstudios.engine.util.toJomlVec
import org.emberstudios.renderer.*

class EditorLayer : Layer {

	companion object {
		const val SCENE_FILEPATH = "assets\\scenes\\main.emsc"

		private val LOGGER = getLogger<EditorLayer>()
	}

	private val editorContext = EditorContext()

	private val gameRuntime get() = Engine.gameRuntime

	private lateinit var camera: Camera
	private lateinit var framebuffer: Framebuffer

	override fun onAttach() {
		camera = Camera()
		camera.setProjection(16f, 16f / 9f, .1f, 100f)

		framebuffer = Framebuffer.create(Framebuffer.Specs(1, 1))

		editorContext.registerWindow(ConsoleWindow(), true)
		editorContext.registerWindow(ViewportWindow(framebuffer, this), true)
		editorContext.registerWindow(InspectorWindow(), true)
		editorContext.registerWindow(SceneHierarchyWindow(), true)
		editorContext.registerWindow(FileExplorerWindow("assets"), true)
		editorContext.registerWindow(TextEditorWindow(), false)
		editorContext.registerWindow(ServerConnectWindow(), false)
		editorContext.registerWindow(ServerConnectWindow.SuccessWindow(), false)
		editorContext.registerWindow(ServerInfoWindow(), false)
		editorContext.registerWindow(ControllerTestWindow(), false)
		editorContext.registerWindow(BuildDistributionWindow(), false)
		editorContext.registerWindow(BuildDistributionWindow.SuccessWindow(), false)

		loadScene()
		editorContext.init()
	}

	fun saveScene() = SceneManager.saveSceneToFile(SCENE_FILEPATH)

	fun loadScene() {
		SceneManager.loadSceneFromFile(SCENE_FILEPATH)
		initScene()
	}

	fun initScene() {
		SceneManager.init()
	}

	override fun onDetach() {
		gameRuntime.stop()
		NetworkingManager.cleanup()
		SceneManager.saveSceneToFile(SCENE_FILEPATH)
		editorContext.saveConfig()
	}

	override fun onUpdate(deltaTime: Float) {
		Engine.gameRuntime.update(deltaTime)

		editorContext.update(deltaTime)
	}

	override fun onRender() {
		Renderer.clear(ImGui.getStyleColorVec4(ImGuiCol.WindowBg).toJomlVec())

		framebuffer.bind()

		Renderer.clear(.1f, .2f, .3f, 1f)

		Renderer.beginScene(camera)
		SceneManager.render()
		Renderer.endScene()

		framebuffer.unbind()
	}

	override fun onRenderImGui() {
		ImGui.dockSpaceOverViewport(ImGui.getMainViewport(), ImGuiDockNodeFlags.PassthruCentralNode)

		if (ImGui.beginMainMenuBar()) {
			val connectedToServer = NetworkingManager.isConnected

			if (ImGui.beginMenu("File")) {
				if (ImGui.menuItem("Build Distribution"))
					editorContext.showCentered<BuildDistributionWindow>(ImVec2(300f, 200f))

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
					NetworkingManager.disconnect()
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