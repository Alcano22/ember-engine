package org.emberstudios.core.input

interface MouseCallback {
	fun onMouseButton(button: Int, action: InputAction)
	fun onMouseMove(x: Float, y: Float)
	fun onScroll(offsetX: Float, offsetY: Float)
}