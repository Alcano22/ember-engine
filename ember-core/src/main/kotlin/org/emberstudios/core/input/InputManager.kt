package org.emberstudios.core.input

interface InputManager {
	fun getKeyboardCallback(): KeyboardCallback
	fun getMouseCallback(): MouseCallback
}