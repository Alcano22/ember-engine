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

	fun onKey(keyCode: Int, action: Int) {
		if (keyCode !in 0..NUM_KEYS) return

		when (action) {
			GLFW_PRESS -> pressedKeys[keyCode] = true
			GLFW_RELEASE -> pressedKeys[keyCode] = false
		}
	}

	fun getKey(key: Key): Boolean {
		val code = key.code
		if (code !in 0..NUM_KEYS) {
			LOGGER.warn { "Invalid key: '$code'" }
			return false
		}

		return currentKeys[code]
	}

	fun getKeyDown(key: Key): Boolean {
		val code = key.code
		if (code !in 0..NUM_KEYS) {
			LOGGER.warn { "Invalid key: '$code'" }
			return false
		}

		return downKeys[code]
	}

	fun getKeyUp(key: Key): Boolean {
		val code = key.code
		if (code !in 0..NUM_KEYS) {
			LOGGER.warn { "Invalid key: '$code'" }
			return false
		}

		return upKeys[code]
	}

}