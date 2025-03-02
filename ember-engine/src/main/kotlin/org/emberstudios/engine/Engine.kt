package org.emberstudios.engine

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
import org.emberstudios.window.Window
import org.emberstudios.core.window.WindowAPIType
import org.emberstudios.engine.event.AppQuitEvent
import org.emberstudios.engine.event.EventBus
import org.emberstudios.engine.event.WindowResizeEvent
import kotlin.system.exitProcess

object Engine {

	private val LOGGER = getLogger<Engine>()

	lateinit var window: Window
		private set

	private lateinit var renderContext: RenderContext

	private lateinit var layerStack: LayerStack
	private lateinit var imGuiLayer: ImGuiLayer

	fun run() {
		init()
		loop()

		EventBus.dispatch(AppQuitEvent())

		cleanup()

		exitProcess(0)
	}

	fun quit() {
		window.quit()
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

		window.setResizeCallback { width, height ->
			layerStack.onWindowResize(width, height)
			EventBus.dispatch(WindowResizeEvent(width, height))
		}

		layerStack.pushLayer(EditorLayer())

		imGuiLayer = ImGuiLayer(window, renderContext)
		layerStack.pushOverlay(imGuiLayer)

		EventBus.initializeAsync()
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