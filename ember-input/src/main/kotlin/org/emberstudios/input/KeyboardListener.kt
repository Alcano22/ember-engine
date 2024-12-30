package org.emberstudios.input

import org.emberstudios.core.input.InputAction
import org.emberstudios.core.input.KeyboardCallback
import org.emberstudios.core.logger.getLogger

class KeyboardListener : KeyboardCallback {

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

	override fun onKey(key: Int, action: InputAction) = when (action) {
		InputAction.PRESS -> pressedKeys[key] = true
		InputAction.RELEASE -> pressedKeys[key] = false
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