package org.emberstudios.core.input

interface KeyboardCallback {
	fun onKey(key: Int, action: InputAction)
}