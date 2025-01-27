package org.emberstudios.engine.gameobject.component

import kotlinx.serialization.Serializable
import org.emberstudios.core.io.ResourceManager
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.util.Time
import org.emberstudios.renderer.Texture
import org.emberstudios.renderer.loadTexture

@Serializable
class Animator : Component() {

    @Serializable
    data class Animation(
        val frames: Array<Texture>,
        val delay: Float
    )

    companion object {
        private val LOGGER = getLogger<Animator>()
    }

    private var spriteRenderer: SpriteRenderer? = null

    private val animations = mutableMapOf<String, Animation>()

    private var currentID: String? = null
    private var index = 0
    private var nextStepTime = 0f

    override fun init() {
        spriteRenderer = getComponent<SpriteRenderer>()
    }

    override fun update(deltaTime: Float) {
        if (currentID == null) return

        val animation = animations[currentID]!!

        if (nextStepTime <= Time.time) {
            nextStepTime = Time.time + animation.delay

            if (index + 1 == animations.size)
                index = 0
            else
                index++

            val frame = animation.frames[index]
            spriteRenderer?.texture = frame
        }
    }

    fun set(id: String) {
        if (id !in animations) {
            LOGGER.error { "Undefined animation id: '$id'" }
            return
        }

        currentID = id
    }

    fun register(id: String, framePaths: Array<String>, delay: Float) {
        val frames = Array<Texture>(framePaths.size) {
            ResourceManager.loadTexture(framePaths[it])
        }

        register(id, frames, delay)
    }

    fun register(id: String, frames: Array<Texture>, delay: Float) {
        animations[id] = Animation(frames, delay)
    }

}