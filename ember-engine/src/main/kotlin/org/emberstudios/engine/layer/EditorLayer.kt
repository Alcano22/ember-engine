package org.emberstudios.engine.layer

import imgui.ImGui
import imgui.ImVec2
import imgui.flag.ImGuiDockNodeFlags
import org.emberstudios.core.io.ResourceManager
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.Engine
import org.emberstudios.engine.gameobject.GameObject
import org.emberstudios.engine.gameobject.component.SpriteRenderer
import org.emberstudios.engine.gameobject.component.TestComponent
import org.emberstudios.engine.util.Time
import org.emberstudios.input.Input
import org.emberstudios.renderer.*
import org.joml.Vector2f
import kotlin.math.round

class EditorLayer : Layer {

	companion object {
		private val LOGGER = getLogger<EditorLayer>()
	}

	private val gameObjects = mutableListOf<GameObject>()

	private lateinit var camera: Camera
	private lateinit var framebuffer: Framebuffer

	private var fps = 0
	private var nextUpdateFPSTime = 0f

	override fun onAttach() {
		camera = Camera()
		camera.setProjection(16f, 16f / 9f, .1f, 100f)

		framebuffer = Framebuffer.create(Framebuffer.Specs(1, 1))

		gameObjects += GameObject("Player").apply {
			addComponent<TestComponent>()
			addComponent<SpriteRenderer> {
				texture = ResourceManager.loadTexture("assets/textures/link.png")
			}
		}

		gameObjects.forEach { it.init() }
	}

	override fun onUpdate(deltaTime: Float) {
		gameObjects.forEach { it.update(deltaTime) }

		if (nextUpdateFPSTime <= Time.time) {
			nextUpdateFPSTime = Time.time + 1f
			fps = round(1f / deltaTime).toInt()
		}
	}

	override fun onRender() {
		framebuffer.bind()

		Renderer.clear(.1f, .2f, .3f, 1f)

		Renderer.beginScene(camera)

		gameObjects.forEach { it.render() }

		Renderer.endScene()

		framebuffer.unbind()
	}

	override fun onRenderImGui() {
		ImGui.dockSpaceOverViewport(ImGui.getMainViewport(), ImGuiDockNodeFlags.PassthruCentralNode)

		ImGui.begin("Scene Info")
		ImGui.text("FPS: $fps")
		val texID = gameObjects[0].getComponent<SpriteRenderer>()!!.texture!!.getID()
		ImGui.image(texID.toLong(), 128f, 128f)
		ImGui.end()

		ImGui.begin("Viewport")

		val viewportFocused = ImGui.isWindowFocused()
		Input.blockingCallback = {
			val io = ImGui.getIO()

			val blockKeyboard = io.wantCaptureKeyboard && !viewportFocused
			val blockMouse = io.wantCaptureMouse && !viewportFocused

			Pair(blockKeyboard, blockMouse)
		}

		val viewportSize = getViewportLargestSize()
		if (framebuffer.width != viewportSize.x.toInt() || framebuffer.height != viewportSize.y.toInt()) {
			framebuffer.resize(viewportSize.x.toInt(), viewportSize.y.toInt())
			Renderer.viewport(viewportSize.x.toInt(), viewportSize.y.toInt())
		}

		val viewportPos = getViewportCenteredPosition(viewportSize)

		ImGui.setCursorPos(viewportPos)
		val textureID = framebuffer.colorAttachmentID.toLong()
		ImGui.image(textureID, viewportSize, ImVec2(0f, 1f), ImVec2(1f, 0f))

		ImGui.end()
	}

	override fun onWindowResize(width: Int, height: Int) = framebuffer.resize(width, height)

	private fun getViewportLargestSize(): ImVec2 {
		val windowSize = ImGui.getContentRegionAvail()
		windowSize.x -= ImGui.getScrollX()
		windowSize.y -= ImGui.getScrollY()

		val aspectRatio = 16f / 9f

		var aspectWidth = windowSize.x
		var aspectHeight = aspectWidth / aspectRatio
		if (aspectHeight > windowSize.y) {
			aspectHeight = windowSize.y
			aspectWidth = aspectHeight * aspectRatio
		}

		return ImVec2(aspectWidth, aspectHeight)
	}

	private fun getViewportCenteredPosition(aspectSize: ImVec2): ImVec2 {
		val windowSize = ImGui.getContentRegionAvail()
		windowSize.x -= ImGui.getScrollX()
		windowSize.y -= ImGui.getScrollY()

		val viewportX = (windowSize.x - aspectSize.x) / 2f
		val viewportY = (windowSize.y - aspectSize.y) / 2f

		return ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY())
	}

}