package org.emberstudios.input

import org.emberstudios.core.logger.getLogger
import org.lwjgl.glfw.GLFW.*

internal class KeyboardListener {

	companion object {
		const val NUM_KEYS = 350

		val LOGGER = getLogger<KeyboardListener>()
	}

	private val pressedKeys = BooleanArray(NUM_KEYS)
	private val currentKeys = BooleanArray(NUM_KEYS)
	private val downKeys = BooleanArray(NUM_KEYS)
	private val upKeys = BooleanArray(NUM_KEYS)

	fun endFrame() {
		for (i in 0..<NUM_KEYS) {
			upKeys[i] = !pressedKeys[i] && currentKeys[i]
			downKeys[i] = pressedKeys[i] && !currentKeys[i]
			currentKeys[i] = pressedKeys[i]
		}
	}

	fun onKey(key: Int, action: Int) {
		if (key >= NUM_KEYS) return

		when (action) {
			GLFW_PRESS -> pressedKeys[key] = true
			GLFW_RELEASE -> pressedKeys[key] = false
		}
	}

	fun getKey(key: Int): Boolean {
		if (key >= NUM_KEYS) {
			LOGGER.warn { "Invalid key: '$key'" }
			return false
		}

		return currentKeys[key]
	}

	fun getKeyDown(key: Int): Boolean {
		if (key >= NUM_KEYS) {
			LOGGER.warn { "Invalid key: '$key'" }
			return false
		}

		return downKeys[key]
	}

	fun getKeyUp(key: Int): Boolean {
		if (key >= NUM_KEYS) {
			LOGGER.warn { "Invalid key: '$key'" }
			return false
		}

		return upKeys[key]
	}

}