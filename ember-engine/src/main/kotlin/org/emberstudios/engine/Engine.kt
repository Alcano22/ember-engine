package org.emberstudios.engine

import org.emberstudios.core.io.ResourceManager
import org.emberstudios.core.logger.getLogger
import org.emberstudios.core.renderer.GraphicsAPIType
import org.emberstudios.core.thread.createThread
import org.emberstudios.core.window.WindowAPIType
import org.emberstudios.engine.event.AppQuitEvent
import org.emberstudios.engine.event.EventBus
import org.emberstudios.engine.event.WindowResizeEvent
import org.emberstudios.engine.layer.EditorLayer
import org.emberstudios.engine.layer.GameLayer
import org.emberstudios.engine.layer.ImGuiLayer
import org.emberstudios.engine.layer.LayerStack
import org.emberstudios.engine.runtime.GameRuntime
import org.emberstudios.engine.util.Time
import org.emberstudios.input.Input
import org.emberstudios.renderer.RenderContext
import org.emberstudios.renderer.Renderer
import org.emberstudios.window.Window
import java.awt.Desktop
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipInputStream
import kotlin.system.exitProcess

object Engine {

	private val IS_EDITOR = System.getProperty("ember.editor")?.toBoolean() ?: false

	private val LOGGER = getLogger<Engine>()

	lateinit var window: Window
		private set
	lateinit var gameRuntime: GameRuntime
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
		if (!window.init("Ember Engine", 1200, (1200f / (16f / 9f)).toInt()))
			return

		renderContext = window.createRenderContext()
		renderContext.init()

		Renderer.init(
			GraphicsAPIType.OPEN_GL,
			renderContext,
			if (IS_EDITOR) "logs/" else null,
			if (IS_EDITOR) "logs/" else null
		)

		layerStack = LayerStack()

		window.setResizeCallback { width, height ->
			layerStack.onWindowResize(width, height)
			EventBus.dispatch(WindowResizeEvent(width, height))
		}

		if (IS_EDITOR) {
			val editorLayer = EditorLayer()
			gameRuntime = GameRuntime(false, editorLayer)
			layerStack.pushLayer(EditorLayer())

			imGuiLayer = ImGuiLayer(window, renderContext)
			layerStack.pushOverlay(imGuiLayer)
		} else {
			gameRuntime = GameRuntime(true)
			layerStack.pushLayer(GameLayer())
		}

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

			if (IS_EDITOR) {
				imGuiLayer.begin()
				layerStack.renderImGui()
				imGuiLayer.end()
			}

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

	fun buildDistribution(
		name: String,
		minMemoryMB: Int,
		maxMemoryMB: Int,
		iconPath: String,
		runAfterBuild: Boolean = false,
		onFinish: () -> Unit = {}
	) {
		val workingDir = File(System.getProperty("user.dir"))
		LOGGER.debug { "Build working dir: '$workingDir'" }

		createThread(
			name = "Build Distribution",
			start = true,
			action = {
				try {
					val originalConfigFile = File("launch4j/launch4j.xml")
					if (!originalConfigFile.exists()) {
						LOGGER.error { "Launch4j config not found: '${originalConfigFile.absolutePath}'" }
						return@createThread
					}
					val configText = originalConfigFile.readText()

					val modifiedConfigText = configText
						.replace("%DIST_NAME%", name)
						.replace("%MIN_MEM%", minMemoryMB.toString())
						.replace("%MAX_MEM%", maxMemoryMB.toString())
						.replace("%ICON_PATH%", iconPath)

					val tmpConfigFile = File(workingDir, "launch4j/launch4j-tmp.xml")
					tmpConfigFile.writeText(modifiedConfigText)
					LOGGER.debug { "Modified Launch4j config written to: '${tmpConfigFile.absolutePath}'" }

					val distDir = File(workingDir, "dist")
					if (!distDir.exists())
						distDir.mkdirs()

					val assetsDir = File(workingDir, "assets")
					if (assetsDir.exists()) {
						val distAssetsDir = File(distDir, "assets")
						if (distAssetsDir.exists())
							distAssetsDir.deleteRecursively()
						assetsDir.copyRecursively(distAssetsDir)
					}

					val bundledJreSource = File(workingDir, "bundledJRE")
					if (!bundledJreSource.exists()) {
						LOGGER.warn { "Bundled JRE not found at '${bundledJreSource.absolutePath}'. Downloading Eclipse Temurin JRE..." }
						val temurinUrl = "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.8+7/OpenJDK17U-jre_x64_windows_hotspot_17.0.8_7.zip"
						downloadAndExtract(temurinUrl, bundledJreSource)
						if (!bundledJreSource.exists()) {
							LOGGER.error { "Bundled JRE not found at: '${bundledJreSource.absolutePath}'" }
							return@createThread
						}
					}

					val bundledJreDest = File(distDir, "jre")
					if (bundledJreDest.exists()) bundledJreDest.deleteRecursively()
					bundledJreSource.copyRecursively(bundledJreDest)
					LOGGER.debug { "Bundled JRE copied to: '${bundledJreDest.absolutePath}'" }

					val launch4jExe = File(workingDir, "launch4j/launch4jc.exe")
					if (!launch4jExe.exists()) {
						LOGGER.error { "Launch4j executable not found: '${launch4jExe.absolutePath}'" }
						return@createThread
					}

					val command = listOf(launch4jExe.absolutePath, tmpConfigFile.absolutePath)
					LOGGER.debug { "Running Launch4j command: ${command.joinToString(" ")}" }
					val process = ProcessBuilder(command)
						.directory(workingDir)
						.inheritIO()
						.start()
					val exitCode = process.waitFor()
					if (exitCode == 0) {
						LOGGER.debug { "Launch4j build successful." }
						if (runAfterBuild) {
							val exeFile = File(workingDir, "dist/Game.exe")
							if (exeFile.exists())
								Runtime.getRuntime().exec(exeFile.absolutePath)
							else
								LOGGER.error { "Executable not found at expected location: '${exeFile.absolutePath}'" }
						} else
							Desktop.getDesktop().browse(distDir.toURI())
					} else
						LOGGER.error { "Launch4j build failed with exit code $exitCode" }
				} catch (e: Exception) {
					e.printStackTrace()
				}
			},
			onFinish = {
				File(workingDir, "launch4j/launch4j-tmp.xml").delete()
				onFinish()
			}
		)
	}

	private fun downloadAndExtract(url: String, targetDir: File) {
		try {
			val tmpZip = File.createTempFile("jre-download", ".zip")
			URL(url).openStream().use { input ->
				Files.copy(input, tmpZip.toPath(), StandardCopyOption.REPLACE_EXISTING)
			}
			ZipInputStream(tmpZip.inputStream()).use { zis ->
				var entry = zis.nextEntry
				while (entry != null) {
					val newFile = File(targetDir, entry.name)
					if (entry.isDirectory)
						newFile.mkdirs()
					else {
						newFile.parentFile.mkdirs()
						newFile.outputStream().use { fos -> zis.copyTo(fos) }
					}
					zis.closeEntry()
					entry = zis.nextEntry
				}
			}
			tmpZip.delete()

			val files = targetDir.listFiles()
			if (files != null && files.size == 1 && files[0].isDirectory) {
				val subDir = files[0]
				subDir.listFiles()?.forEach { file ->
					file.copyRecursively(File(targetDir, file.name), overwrite = true)
				}
				subDir.deleteRecursively()
			}

			LOGGER.debug { "Downloaded and extracted JRE to '${targetDir.absolutePath}'" }
		} catch (e: Exception) {
			LOGGER.error { "Failed to download and extract JRE: ${e.message}" }
			e.printStackTrace()
		}
	}

}

fun main() = Engine.run()