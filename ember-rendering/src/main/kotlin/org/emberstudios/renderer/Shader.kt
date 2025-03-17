package org.emberstudios.renderer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.emberstudios.core.io.Resource
import org.emberstudios.core.io.ResourceManager
import org.emberstudios.core.logger.exitError
import org.emberstudios.core.logger.getLogger
import org.emberstudios.core.renderer.GraphicsAPIType
import org.emberstudios.renderer.opengl.GLShader

/**
 * A shader that can be used to render objects.
 */
interface Shader : Resource {
	companion object {
		private val FACTORY_LOGGER = KotlinLogging.logger("ShaderFactory")

		/**
		 * Creates a new shader with the given [vertexSrc] and [fragmentSrc].
		 *
		 * @param vertexSrc The vertex shader source.
		 * @param fragmentSrc The fragment shader source.
		 *
		 * @return The created shader.
		 */
		fun create(vertexSrc: String, fragmentSrc: String): Shader = when (Renderer.apiType) {
			GraphicsAPIType.OPEN_GL -> GLShader(vertexSrc, fragmentSrc)
			GraphicsAPIType.VULKAN -> FACTORY_LOGGER.exitError { "Vulkan is not supported!" }
		}

		/**
		 * Creates a new shader with the given [filepath].
		 *
		 * @param filepath The path to the shader file.
		 *
		 * @return The created shader.
		 */
		fun create(filepath: String): Shader = when (Renderer.apiType) {
			GraphicsAPIType.OPEN_GL -> GLShader(filepath)
			GraphicsAPIType.VULKAN -> FACTORY_LOGGER.exitError { "Vulkan is not supported!" }
		}
	}

	/**
	 * Loads the shader from the file.
	 */
	fun loadFromFile()

	/**
	 * Compiles the shader.
	 */
	fun compile()

	/**
	 * Reloads the shader.
	 */
	fun reload()

	/**
	 * Sets the uniform with the given [name] to the given [value].
	 */
	fun <T> setUniform(name: String, value: T)

	/**
	 * Binds the shader.
	 */
	fun bind()

	/**
	 * Unbinds the shader.
	 */
	fun unbind()
}

/**
 * Loads the shader from the given [filepath].
 */
fun ResourceManager.loadShader(filepath: String): Shader {
	val (shader, cached) = load(filepath) { Shader.create(it) }
	if (!cached)
		getLogger<ResourceManager>().trace { "Loaded shader: '$filepath'" }

	ShaderLibrary.registerShader(filepath, shader)
	return shader
}