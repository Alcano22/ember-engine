package org.emberstudios.renderer

import org.emberstudios.renderer.opengl.OpenGLRenderAPI

enum class RenderAPIType(private val apiFactory: () -> RenderAPI) {
	OPEN_GL({ OpenGLRenderAPI() }),
	VULKAN({ throw UnsupportedOperationException("Vulkan is not supported!") });

	fun create() = apiFactory()
}