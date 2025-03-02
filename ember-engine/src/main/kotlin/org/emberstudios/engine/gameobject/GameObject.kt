package org.emberstudios.engine.gameobject

import imgui.ImGui
import imgui.type.ImBoolean
import imgui.type.ImString
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.emberstudios.core.utils.GID
import org.emberstudios.engine.editor.Inspectable
import org.emberstudios.engine.gameobject.component.Component
import org.emberstudios.engine.gameobject.component.ExposeInInspector
import org.emberstudios.engine.gameobject.component.Transform
import org.emberstudios.engine.serialization.GameObjectSerializer
import org.emberstudios.engine.util.Color
import org.emberstudios.core.util.toDisplayStyle
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector3f
import org.joml.Vector3i
import org.joml.Vector4f
import org.joml.Vector4i
import kotlin.reflect.*
import kotlin.reflect.full.createInstance
import kotlin.reflect.jvm.isAccessible

@Serializable(with = GameObjectSerializer::class)
class GameObject(
	var name: String = "GameObject",
	position: Vector2f = Vector2f(),
	rotation: Float = 0f,
	scale: Vector2f = Vector2f(1f, 1f)
) : Inspectable {

	var components = mutableListOf<Component>()
	var gid = GID()

	val transform get() = components[0] as Transform

	@Transient private var initialized = false

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

	@Suppress("UNCHECKED_CAST")
	override fun inspect() {
		val imName = ImString(name, 64)
		if (ImGui.inputText("Name", imName))
			name = imName.get()

		ImGui.dummy(0f, 50f)

		for (component in components) {
			val kClass = component::class
			val displayName = kClass.simpleName!!.toDisplayStyle()
			if (ImGui.collapsingHeader(displayName)) {
				for (property in kClass.members) {
					if (property !is KProperty1<*, *>) continue

					val isPublic = property.visibility == KVisibility.PUBLIC
					val hasAnnotation = property.annotations.any { it is ExposeInInspector }

					if (!isPublic && !hasAnnotation) continue

					property.isAccessible = true

					when (property.returnType.classifier) {
						Boolean::class -> {
							val imBool = ImBoolean(property.getter.call(component) as Boolean)
							if (ImGui.checkbox(property.name.toDisplayStyle(), imBool))
								(property as KMutableProperty1<Component, Boolean>)
									.setter.call(component, imBool.get())
						}
						Int::class -> {
							val imInt = intArrayOf(property.getter.call(component) as Int)
							if (ImGui.dragInt(property.name.toDisplayStyle(), imInt))
								(property as KMutableProperty1<Component, Int>)
									.setter.call(component, imInt[0])
						}
						Float::class -> {
							val imFloat = floatArrayOf(property.getter.call(component) as Float)
							if (ImGui.dragFloat(property.name.toDisplayStyle(), imFloat, 0.01f))
								(property as KMutableProperty1<Component, Float>)
									.setter.call(component, imFloat[0])
						}
						Vector2i::class -> {
							val vec2i = property.getter.call(component) as Vector2i
							val imVec2i = intArrayOf(vec2i.x, vec2i.y)
							if (ImGui.dragInt2(property.name.toDisplayStyle(), imVec2i))
								(property as KMutableProperty1<Component, Vector2i>)
									.setter.call(component, Vector2i(imVec2i[0], imVec2i[1]))
						}
						Vector3i::class -> {
							val vec3i = property.getter.call(component) as Vector3i
							val imVec3i = intArrayOf(vec3i.x, vec3i.y, vec3i.z)
							if (ImGui.dragInt3(property.name.toDisplayStyle(), imVec3i))
								(property as KMutableProperty1<Component, Vector3i>)
									.setter.call(component, Vector3i(imVec3i[0], imVec3i[1], imVec3i[2]))
						}
						Vector4i::class -> {
							val vec4i = property.getter.call(component) as Vector4i
							val imVec4i = intArrayOf(vec4i.x, vec4i.y, vec4i.z, vec4i.w)
							if (ImGui.dragInt4(property.name.toDisplayStyle(), imVec4i))
								(property as KMutableProperty1<Component, Vector4i>)
									.setter.call(component, Vector4i(imVec4i[0], imVec4i[1], imVec4i[2], imVec4i[3]))
						}
						Vector2f::class -> {
							val vec2f = property.getter.call(component) as Vector2f
							val imVec2f = floatArrayOf(vec2f.x, vec2f.y)
							if (ImGui.dragFloat2(property.name.toDisplayStyle(), imVec2f, 0.01f))
								(property as KMutableProperty1<Component, Vector2f>)
									.setter.call(component, Vector2f(imVec2f[0], imVec2f[1]))
						}
						Vector3f::class -> {
							val vec3f = property.getter.call(component) as Vector3f
							val imVec3f = floatArrayOf(vec3f.x, vec3f.y, vec3f.z)
							if (ImGui.dragFloat3(property.name.toDisplayStyle(), imVec3f, 0.01f))
								(property as KMutableProperty1<Component, Vector3f>)
									.setter.call(component, Vector3f(imVec3f[0], imVec3f[1], imVec3f[2]))
						}
						Vector4f::class -> {
							val vec4f = property.getter.call(component) as Vector4f
							val imVec4f = floatArrayOf(vec4f.x, vec4f.y, vec4f.z, vec4f.w)
							if (ImGui.dragFloat4(property.name.toDisplayStyle(), imVec4f, 0.01f))
								(property as KMutableProperty1<Component, Vector4f>)
									.setter.call(component, Vector4f(imVec4f[0], imVec4f[1], imVec4f[2], imVec4f[3]))
						}
						Color::class -> {
							val color = property.getter.call(component) as Color
							val imColor = floatArrayOf(color.r, color.g, color.b, color.a)
							if (ImGui.colorEdit4(property.name.toDisplayStyle(), imColor))
								(property as KMutableProperty1<Component, Color>)
									.setter.call(component, Color(imColor[0], imColor[1], imColor[2], imColor[3]))
						}
						String::class -> {
							val str = property.getter.call(component) as String
							val imStr = ImString(str)
							if (ImGui.inputText(property.name.toDisplayStyle(), imStr))
								(property as KMutableProperty1<Component, String>)
									.setter.call(component, imStr.get())
						}
					}

					property.isAccessible = false
				}

				ImGui.dummy(0f, 25f)
			}
		}
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