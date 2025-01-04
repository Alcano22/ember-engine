package org.emberstudios.engine

import imgui.ImGui
import org.emberstudios.core.io.ResourceManager
import org.emberstudios.core.logger.getLogger
import org.emberstudios.core.renderer.GraphicsAPIType
import org.emberstudios.engine.layer.ImGuiLayer
import org.emberstudios.engine.layer.LayerStack
import org.emberstudios.engine.layer.EditorLayer
import org.emberstudios.engine.util.Time
import org.emberstudios.input.Input
import org.emberstudios.renderer.RenderContext
import org.emberstudios.renderer.Renderer
import org.emberstudios.window.InputHandler
import org.emberstudios.window.Window
import org.emberstudios.core.window.WindowAPIType
import org.emberstudios.engine.layer.Layer

object Engine {

	private val LOGGER = getLogger<Engine>()

	lateinit var window: Window
		private set

	private lateinit var renderContext: RenderContext
	private lateinit var inputHandler: InputHandler

	private lateinit var layerStack: LayerStack
	private lateinit var imGuiLayer: ImGuiLayer

	fun run() {
		init()
		loop()
		cleanup()
	}

	private fun init() {
		window = Window.create(WindowAPIType.GLFW)
		if (!window.init("Ember Engine", 1200, 900))
			return

		renderContext = window.createRenderContext()
		renderContext.init()

		Renderer.init(
			GraphicsAPIType.OPEN_GL,
			renderContext,
			"logs/",
			"logs/"
		)

		layerStack = LayerStack()

		window.setResizeCallback { width, height -> layerStack.onWindowResize(width, height) }

		inputHandler = InputHandler.create(Input)
		inputHandler.init(window.nativeHandle)

		layerStack.pushLayer(EditorLayer())

		imGuiLayer = ImGuiLayer(window, renderContext)
		layerStack.pushOverlay(imGuiLayer)
	}

	private fun loop() {
		var lastTime = 0f
		while (!window.shouldClose) {
			Time.time = window.time

			val currentTime = Time.time
			Time.deltaTime = (currentTime - lastTime)
			lastTime = currentTime

			layerStack.update(Time.deltaTime)
			layerStack.render()

			imGuiLayer.begin()
			layerStack.renderImGui()
			imGuiLayer.end()

			Input.endFrame()

			renderContext.swapBuffers()
			window.update()
		}
	}

	private fun cleanup() {
		layerStack.cleanup()
		ResourceManager.cleanup()

		window.destroy()
	}

}

fun main() = Engine.run()