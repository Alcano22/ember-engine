package org.emberstudios.engine.layer

class LayerStack : Iterable<Layer> {

	private val layers = mutableListOf<Layer>()

	private var layerInsertIndex = 0

	fun pushLayer(layer: Layer) {
		layers.add(layerInsertIndex, layer)
		layerInsertIndex++
		layer.onAttach()
	}

	fun pushOverlay(overlay: Layer) {
		layers += overlay
		overlay.onAttach()
	}

	fun popLayer(layer: Layer) {
		val index = layers.indexOf(layer)
		if (index >= layerInsertIndex) return

		layer.onDetach()
		layers.removeAt(index)
		layerInsertIndex--
	}

	fun popOverlay(overlay: Layer) {
		val index = layers.indexOf(overlay)
		if (index < layerInsertIndex) return

		overlay.onDetach()
		layers.removeAt(index)
	}

	fun update(deltaTime: Float) = layers.forEach { it.onUpdate(deltaTime) }
	fun render() = layers.forEach { it.onRender() }
	fun renderImGui() = layers.forEach { it.onImGuiRender() }

	fun cleanup() {
		layers.forEach { it.onDetach() }
		layers.clear()
	}

	override fun iterator() = layers.iterator()

}