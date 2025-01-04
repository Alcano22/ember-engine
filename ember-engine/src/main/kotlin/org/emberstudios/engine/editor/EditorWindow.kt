package org.emberstudios.engine.editor

import imgui.ImGui
import imgui.flag.ImGuiWindowFlags
import kotlin.reflect.full.declaredFunctions

abstract class EditorWindow(
	val name: String,
	var showing: Boolean = false,
	var flags: Int = 0,
) {

	fun render() {
		if (!showing) return

		val hasMenuBar = hasMenuBar()

		val flags = if (hasMenuBar) ImGuiWindowFlags.MenuBar or flags else flags
		ImGui.begin(name, flags)

		if (hasMenuBar) {
			ImGui.beginMenuBar()
			renderMenuBar()
			ImGui.endMenuBar()
		}

		renderContent()

		ImGui.end()
	}

	open fun init() {}
	open fun update(deltaTime: Float) {}

	open fun renderMenuBar() {}
	protected abstract fun renderContent()

	private fun hasMenuBar() = this::class.declaredFunctions
		.firstOrNull { it.name == "renderMenuBar" }
		?.isOpen == true
}