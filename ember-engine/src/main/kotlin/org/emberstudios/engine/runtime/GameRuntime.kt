package org.emberstudios.engine.runtime

import org.emberstudios.engine.layer.EditorLayer
import org.emberstudios.engine.physics.PhysicsManager
import org.emberstudios.engine.scene.SceneManager

class GameRuntime(
	playing: Boolean,
	private val editorLayer: EditorLayer? = null
) {

	enum class State {
		STOPPED,
		PLAYING,
		PAUSED
	}

	companion object {
		const val FIXED_TIMESTEP = 1f / 60f
	}

	var state = State.STOPPED
		private set

	val isPaused get() = state == State.PAUSED
	val isPlayingOrPaused get() = state == State.PLAYING || isPaused

	private var physicsAccumulator = 0f

	init {
		if (playing) play()
	}

	fun update(deltaTime: Float) {
		SceneManager.update(deltaTime)

		physicsAccumulator += deltaTime
		while (physicsAccumulator >= FIXED_TIMESTEP) {
			SceneManager.fixedUpdate(FIXED_TIMESTEP)
			PhysicsManager.update(FIXED_TIMESTEP)
			physicsAccumulator -= FIXED_TIMESTEP
		}
	}

	fun render() {

	}

	fun play() {
		if (isPlayingOrPaused) return

		state = State.PLAYING
		editorLayer?.saveScene()
		PhysicsManager.init()
		editorLayer?.initScene()
	}

	fun pause() {
		if (state == State.STOPPED) return

		state = State.PAUSED
	}

	fun unpause() {
		if (isPaused) return

		state = State.PLAYING
	}

	fun togglePlay() {
		when (state) {
			State.STOPPED -> play()
			State.PLAYING -> stop()
			State.PAUSED -> stop()
		}
	}

	fun togglePause() {
		if (state == State.PLAYING)
			state = State.PAUSED
		else if (state == State.PAUSED)
			state = State.PLAYING
	}

	fun stop() {
		if (isPlayingOrPaused) return

		state = State.STOPPED
		editorLayer?.loadScene()
		PhysicsManager.cleanup()
	}

	fun cleanup() {

	}

}