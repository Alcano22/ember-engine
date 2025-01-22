package org.emberstudios.engine.editor

import org.emberstudios.core.logger.getLogger

interface Inspectable {
	fun inspect()
}

class InspectorWindow(showing: Boolean = false) : EditorWindow("Inspector", showing) {

	companion object {
		val LOGGER = getLogger<InspectorWindow>()

		lateinit var instance: InspectorWindow
			private set

		fun inspect(inspectable: Inspectable?) = instance.inspect(inspectable)
		fun isInspected(inspectable: Inspectable?) = instance.isInspected(inspectable)
	}

	private var inspected: Inspectable? = null

	init {
		instance = this
	}

	override fun renderContent() { inspected?.inspect() }

	fun inspect(inspectable: Inspectable?) { inspected = inspectable }
	fun isInspected(inspectable: Inspectable?) = inspected == inspectable
}