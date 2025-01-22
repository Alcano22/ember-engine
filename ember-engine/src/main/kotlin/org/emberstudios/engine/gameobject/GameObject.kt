package org.emberstudios.engine.gameobject

import imgui.ImGui
import imgui.flag.ImGuiInputTextFlags
import imgui.type.ImString
import org.emberstudios.core.utils.GID
import org.emberstudios.engine.gameobject.component.Component
import org.emberstudios.engine.gameobject.component.Transform
import org.joml.Vector2f
import kotlin.reflect.full.createInstance

class GameObject(
	var name: String = "GameObject",
	position: Vector2f = Vector2f(),
	rotation: Float = 0f,
	scale: Vector2f = Vector2f(1f, 1f)
) {

	val components = mutableListOf<Component>()
	val gid = GID()

	val transform get() = components[0] as Transform

	var initialized = false

	init {
		components += Transform().apply {
			this.position = position
			this.rotation = rotation
			this.scale = scale
		}
	}

	fun init() {
		if (initialized) return

		components.forEach {
			it.gameObject = this
			it.init()
		}
		initialized = true
	}

	fun update(deltaTime: Float) = components.forEach {
		it.gameObject = this
		it.update(deltaTime)
	}

	fun render() = components.forEach { it.render() }

	fun renderImGui() {
		val imName = ImString(name, 64)
		if (ImGui.inputText("Name", imName))
			name = imName.get()
	}

	fun cleanup() = components.forEach { it.cleanup() }

	inline fun <reified T : Component> addComponent(configure: T.() -> Unit = {}): T {
		val component = T::class.createInstance()
		component.configure()
		components += component
		return component
	}

	inline fun <reified T : Component> getComponents() = components.filterIsInstance<T>().toTypedArray()
	inline fun <reified T : Component> getComponent() = getComponents<T>().firstOrNull()

	inline fun <reified T : Component> removeComponents() = components.removeIf { it is T }
	inline fun <reified T : Component> removeComponent() = components.removeAt(components.indexOfFirst { it is T })

}