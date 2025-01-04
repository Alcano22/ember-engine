package org.emberstudios.engine.editor

import org.emberstudios.core.logger.getLogger
import org.emberstudios.core.math.toSimpleString
import org.emberstudios.engine.gameobject.GameObject
import org.emberstudios.input.Input
import org.emberstudios.renderer.Camera

class InspectorWindow(private val cam: Camera, showing: Boolean = false) : EditorWindow("Inspector", showing) {

	companion object {
		val LOGGER = getLogger<InspectorWindow>()
	}

	private var selectedGameObject: GameObject? = null

	override fun renderContent() {
		selectedGameObject?.renderImGui()

		val mouseWorldPos = cam.screenToWorldPoint(Input.mouseViewportPosition)
		LOGGER.debug { mouseWorldPos.toSimpleString() }
	}
}