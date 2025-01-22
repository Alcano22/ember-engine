package org.emberstudios.engine.editor

import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.gameobject.GameObject
import org.emberstudios.renderer.Camera

class InspectorWindow(
	private val cam: Camera, showing: Boolean = false
) : EditorWindow("Inspector", showing) {

	companion object {
		val LOGGER = getLogger<InspectorWindow>()
	}

	private var inspectedGameObject: GameObject? = null

	override fun renderContent() {
		inspectedGameObject?.renderImGui()
	}

	fun inspect(gameObject: GameObject?) { inspectedGameObject = gameObject }
	fun isInspected(gameObject: GameObject?) = inspectedGameObject == gameObject
}