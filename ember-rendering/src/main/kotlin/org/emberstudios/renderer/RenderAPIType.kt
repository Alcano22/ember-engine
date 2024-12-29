package org.emberstudios.renderer

import org.emberstudios.renderer.opengl.GLRenderAPI

enum class RenderAPIType(private val apiFactory: () -> RenderAPI) {
	OPEN_GL({ GLRenderAPI() }),
	VULKAN({ throw UnsupportedOperationException("Vulkan is not supported!") });

	fun create() = apiFactory()
}