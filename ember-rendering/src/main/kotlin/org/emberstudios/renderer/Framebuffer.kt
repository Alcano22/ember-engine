package org.emberstudios.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.logger.exitError
import org.emberstudios.core.renderer.GraphicsAPIType
import org.emberstudios.renderer.opengl.GLFramebuffer

/**
 * A framebuffer is a container for textures that can be rendered to.
 */
abstract class Framebuffer(protected val specs: Specs) {

	/**
	 * The specifications of the framebuffer.
	 */
	data class Specs(
		var width: Int,
		var height: Int,
		var samples: Int = 1,
		var swapChainTarget: Boolean = false
	)

	companion object {
		private val LOGGER = KotlinLogging.logger("FramebufferFactory")

		/**
		 * Creates a new framebuffer.
		 *
		 * @param specs The specifications of the framebuffer.
		 *
		 * @return The created framebuffer.
		 */
		fun create(specs: Specs): Framebuffer = when (Renderer.apiType) {
			GraphicsAPIType.OPEN_GL -> GLFramebuffer(specs)
			GraphicsAPIType.VULKAN -> LOGGER.exitError { "Vulkan is not supported!" }
		}
	}

	val width get() = specs.width
	val height get() = specs.height

	abstract val colorAttachmentID: Int

	abstract fun resize(width: Int, height: Int)

	abstract fun bind()
	abstract fun unbind()

	abstract fun cleanup()

}