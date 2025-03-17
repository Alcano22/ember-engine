package org.emberstudios.engine.editor

import imgui.ImGui
import imgui.ImVec2
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImString
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.Engine
import java.io.File

class BuildDistributionWindow : EditorWindow(
	"Build Distribution",
	true,
	ImGuiWindowFlags.NoResize or
	ImGuiWindowFlags.NoCollapse or
	ImGuiWindowFlags.NoMove or
	ImGuiWindowFlags.NoDocking,
	saveShowingInConfig = false
) {

	class SuccessWindow : EditorWindow(
		"Connection state",
		flags = ImGuiWindowFlags.NoResize or
				ImGuiWindowFlags.NoCollapse or
				ImGuiWindowFlags.NoMove or
				ImGuiWindowFlags.NoDocking,
		saveShowingInConfig = false
	) {
		override fun renderContent() {
			ImGui.text("Built successfully!")

			if (ImGui.button("OK"))
				hide()
		}
	}

	private val imBuildName = ImString(32)
	private var minMemoryGB = 4
	private var maxMemoryGB = 4
	private var imIconPath = ImString(64)

	override fun renderContent() {
		ImGui.inputText("Name", imBuildName)

		val imMemoryGB = intArrayOf(minMemoryGB, maxMemoryGB)
		if (ImGui.dragInt2("Memory", imMemoryGB, 1f, 1, 32, "%d GB")) {
			minMemoryGB = imMemoryGB[0]
			maxMemoryGB = imMemoryGB[1]
		}

		ImGui.inputText("Icon Path", imIconPath)

		val minMemoryMB = minMemoryGB * 1024
		val maxMemoryMB = maxMemoryGB * 1024

		val onFinish = {
			EditorContext.showCentered<SuccessWindow>(ImVec2(200f, 100f))
			hide()
		}

		val iconPath = File("assets/${imIconPath.get()}").absolutePath

		if (ImGui.button("Build"))
			Engine.buildDistribution(
				imBuildName.get(),
				minMemoryMB,
				maxMemoryMB,
				iconPath,
				runAfterBuild = false,
				onFinish = onFinish
			)

		ImGui.sameLine()

		if (ImGui.button("Build & Run"))
			Engine.buildDistribution(
				imBuildName.get(),
				minMemoryMB,
				maxMemoryMB,
				iconPath,
				runAfterBuild = true,
				onFinish = onFinish
			)
	}

	override fun loadConfig() {
		imBuildName.set(loadConfigValue("build_name", "ember-game"))
		minMemoryGB = loadConfigValue("min_memory_gb", 4)
		maxMemoryGB = loadConfigValue("max_memory_gb", 4)
		imIconPath.set(loadConfigValue("icon_path", "textures/icon.ico"))
	}

	override fun saveConfig() {
		saveConfigValue("build_name", imBuildName.get())
		saveConfigValue("min_memory_gb", minMemoryGB)
		saveConfigValue("max_memory_gb", maxMemoryGB)
		saveConfigValue("icon_path", imIconPath.get())
	}

}