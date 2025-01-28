package org.emberstudios.engine.editor

import imgui.ImGui
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImBoolean
import org.emberstudios.engine.util.toSnakeCase
import kotlin.reflect.full.declaredFunctions

abstract class EditorWindow(
	var name: String,
	private var closeable: Boolean = false,
	private var flags: Int = 0,
) {

	private var hasFocus = false

	fun render() {
		val hasMenuBar = hasFunction("renderMenuBar")
		val hasContextMenuPopup = hasFunction("renderContextMenuPopup")

		val flags = if (hasMenuBar) ImGuiWindowFlags.MenuBar or flags else flags

		if (hasFocus) {
			ImGui.setNextWindowFocus()
			hasFocus = false
		}

		if (closeable) {
			val imOpen = ImBoolean(EditorContext.isShown(this))
			if (ImGui.begin(name, imOpen, flags)) {
				EditorContext.setShown(this, imOpen.get())

				if (hasContextMenuPopup && ImGui.beginPopupContextItem()) {
					renderContextMenuPopup()
					ImGui.endPopup()
				}

				if (hasMenuBar) {
					ImGui.beginMenuBar()
					renderMenuBar()
					ImGui.endMenuBar()
				}

				renderContent()
			}
		} else {
			if (ImGui.begin(name, flags)) {
				if (hasContextMenuPopup && ImGui.beginPopupContextItem()) {
					renderContextMenuPopup()
					ImGui.endPopup()
				}

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

	open fun loadConfig() {}
	open fun saveConfig() {}

	protected inline fun <reified T> loadConfigValue(key: String, default: T) =
		EditorConfig.get("${this::class.simpleName!!.toSnakeCase()}.$key", default)
	protected inline fun <reified T> saveConfigValue(key: String, value: T) =
		EditorConfig.set("${this::class.simpleName!!.toSnakeCase()}.$key", value)

	open fun renderMenuBar() {}
	open fun renderContextMenuPopup() {}
	protected abstract fun renderContent()

	private fun hasFunction(functionName: String): Boolean {
		val baseMethod = EditorWindow::class.declaredFunctions
			.firstOrNull { it.name == functionName } ?: return false

		val subMethod = this::class.declaredFunctions
			.firstOrNull { it.name == functionName && it != baseMethod }

		return subMethod != null
	}
}