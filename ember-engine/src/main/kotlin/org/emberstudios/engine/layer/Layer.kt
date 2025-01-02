package org.emberstudios.engine.layer

interface Layer {
	fun onAttach() {}
	fun onDetach() {}
	fun onUpdate(deltaTime: Float) {}
	fun onRender() {}
	fun onRenderImGui() {}

	fun onWindowResize(width: Int, height: Int) {}
}
