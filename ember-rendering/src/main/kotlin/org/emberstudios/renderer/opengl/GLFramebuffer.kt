package org.emberstudios.renderer.opengl

import org.emberstudios.core.logger.exitError
import org.emberstudios.core.logger.getLogger
import org.emberstudios.renderer.Framebuffer
import org.lwjgl.opengl.GL30.*
import java.nio.ByteBuffer

internal class GLFramebuffer(specs: Specs) : Framebuffer(specs) {

	companion object {
		private val LOGGER = getLogger<GLFramebuffer>()
	}

	private var id = 0
	private var colorAttachment = 0
	private var depthAttachment = 0

	override val colorAttachmentID: Int
		get() = colorAttachment

	init {
		invalidate()
	}

	private fun invalidate() {
		if (id != 0)
			cleanup()

		id = glGenFramebuffers()
		glBindFramebuffer(GL_FRAMEBUFFER, id)

		colorAttachment = glGenTextures()
		glBindTexture(GL_TEXTURE_2D, colorAttachment)
		glTexImage2D(
			GL_TEXTURE_2D,
			0,
			GL_RGBA8,
			specs.width,
			specs.height,
			0,
			GL_RGBA,
			GL_UNSIGNED_BYTE,
			null as ByteBuffer?
		)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorAttachment, 0)

		depthAttachment = glGenRenderbuffers()
		glBindRenderbuffer(GL_RENDERBUFFER, depthAttachment)
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, specs.width, specs.height)
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, depthAttachment)

		val status = glCheckFramebufferStatus(GL_FRAMEBUFFER)
		if (status != GL_FRAMEBUFFER_COMPLETE)
			LOGGER.exitError { "Framebuffer is incomplete!" }

		glBindFramebuffer(GL_FRAMEBUFFER, 0)
	}

	override fun resize(width: Int, height: Int) {
		if (width <= 0 || height <= 0)
			LOGGER.exitError { "Attempted to resize framebuffer with invalid dimensions: ${width}x$height" }

		specs.width = width
		specs.height = height
		LOGGER.trace { "Resizing framebuffer to: ${width}x$height" }
		invalidate()
	}

	override fun bind() = glBindFramebuffer(GL_FRAMEBUFFER, id)
	override fun unbind() = glBindFramebuffer(GL_FRAMEBUFFER, 0)

	override fun cleanup() {
		glDeleteFramebuffers(id)
		glDeleteTextures(colorAttachment)
		glDeleteRenderbuffers(depthAttachment)
	}

}