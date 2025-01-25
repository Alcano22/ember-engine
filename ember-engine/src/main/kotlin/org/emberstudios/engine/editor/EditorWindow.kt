package org.emberstudios.engine.editor

import imgui.ImGui
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImBoolean
import kotlin.reflect.full.declaredFunctions

abstract class EditorWindow(
	val name: String,
	var closeable: Boolean = false,
	var flags: Int = 0,
) {

	private var hasFocus = false

	fun render() {
		val hasMenuBar = hasMenuBar()

		val flags = if (hasMenuBar) ImGuiWindowFlags.MenuBar or flags else flags

		if (hasFocus) {
			ImGui.setNextWindowFocus()
			hasFocus = false
		}

		if (closeable) {
			val imOpen = ImBoolean(EditorContext.isShown(this))
			if (ImGui.begin(name, imOpen, flags)) {
				EditorContext.setShown(this, imOpen.get())

				if (hasMenuBar) {
					ImGui.beginMenuBar()
					renderMenuBar()
					ImGui.endMenuBar()
				}

				renderContent()
			}
		} else {
			if (ImGui.begin(name, flags)) {
				if (hasMenuBar) {
					ImGui.beginMenuBar()
					renderMenuBar()
					ImGui.endMenuBar()
				}

				renderContent()
			}
		}

		ImGui.end()
	}

	fun requestFocus() { hasFocus = true }

	open fun init() {}
	open fun update(deltaTime: Float) {}

	open fun renderMenuBar() {}
	protected abstract fun renderContent()

	private fun hasMenuBar() = this::class.declaredFunctions
		.firstOrNull { it.name == "renderMenuBar" }
		?.isOpen == true
}