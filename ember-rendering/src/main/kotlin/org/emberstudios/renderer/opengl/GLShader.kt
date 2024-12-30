package org.emberstudios.renderer.opengl

import org.emberstudios.core.logger.exitError
import org.emberstudios.core.logger.getLogger
import org.emberstudios.core.math.toArray
import org.emberstudios.renderer.Shader
import org.joml.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.system.MemoryStack
import java.io.File

internal class GLShader : Shader {

	companion object {
		const val TYPE_PREFIX = "#type"

		val LOGGER = getLogger<GLShader>()
	}

	private var programID = 0
	private var compiled = false

	private val filepath: String
	private var vertexSrc = ""
	private var fragmentSrc = ""

	constructor(vertexSrc: String, fragmentSrc: String) {
		this.vertexSrc = vertexSrc
		this.fragmentSrc = fragmentSrc
		filepath = "undefined"
	}

	constructor(filepath: String) {
		this.filepath = filepath

		val file = File(filepath)
		if (!file.exists())
			LOGGER.exitError { "File not found: '$filepath'" }

		val src = file.readText()
		val splitSrc = src.split(Regex("($TYPE_PREFIX)( )+([a-zA-Z]+)"))

		var index = src.indexOf(TYPE_PREFIX) + 6
		var eol = src.indexOf("\r\n", index)
		val firstPattern = src.substring(index, eol).trim()

		index = src.indexOf(TYPE_PREFIX, eol) + 6
		eol = src.indexOf("\r\n", index)
		val secondPattern = src.substring(index, eol).trim()

		when (firstPattern) {
			"vertex" -> vertexSrc = splitSrc[1]
			"fragment" -> fragmentSrc = splitSrc[1]
			else -> LOGGER.exitError { "Unexpected token '$firstPattern' in '$filepath'" }
		}

		when (secondPattern) {
			"vertex" -> vertexSrc = splitSrc[2]
			"fragment" -> fragmentSrc = splitSrc[2]
			else -> LOGGER.exitError { "Unexpected token '$secondPattern' in '$filepath'" }
		}
	}

	override fun compile() {
		val vertexShader = compileShader(vertexSrc, GL_VERTEX_SHADER)
		val fragmentShader = compileShader(fragmentSrc, GL_FRAGMENT_SHADER)

		programID = glCreateProgram()
		glAttachShader(programID, vertexShader)
		glAttachShader(programID, fragmentShader)
		glLinkProgram(programID)

		val success = glGetProgrami(programID, GL_LINK_STATUS)
		if (success == GL_FALSE) {
			val infoLog = glGetProgramInfoLog(programID)
			LOGGER.exitError { "Failed to link shader program: $infoLog" }
		}

		glDeleteShader(vertexShader)
		glDeleteShader(fragmentShader)

		compiled = true
	}

	private fun compileShader(src: String, type: Int): Int {
		val shader = glCreateShader(type)
		glShaderSource(shader, src)
		glCompileShader(shader)

		val success = glGetShaderi(shader, GL_COMPILE_STATUS)
		if (success == GL_FALSE) {
			val infoLog = glGetShaderInfoLog(shader)
			LOGGER.exitError { "Failed to compile shader: $infoLog" }
		}

		return shader
	}

	override fun <T> setUniform(name: String, value: T) {
		if (!compiled) {
			LOGGER.error { "Shader is not compiled: '$filepath'" }
			return
		}

		val loc = glGetUniformLocation(programID, name)
		if (loc == -1) {
			LOGGER.error { "Unknown uniform in shader '$filepath': $name" }
			return
		}

		when (value) {
			is Boolean -> setInt(loc, if (value) 1 else 0)
			is Int -> setInt(loc, value)
			is Float -> setFloat(loc, value)
			is IntArray -> setInts(loc, value)
			is FloatArray -> setFloats(loc, value)
			is Vector2i -> setVector2i(loc, value)
			is Vector3i -> setVector3i(loc, value)
			is Vector4i -> setVector4i(loc, value)
			is Vector2f -> setVector2f(loc, value)
			is Vector3f -> setVector3f(loc, value)
			is Vector4f -> setVector4f(loc, value)
			is Matrix3f -> setMatrix3f(loc, value)
			is Matrix4f -> setMatrix4f(loc, value)
			else -> LOGGER.exitError {
				val typename = value!!::class.simpleName
				"Uniform type '$typename' is not supported!"
			}
		}
	}

	private fun setInt(loc: Int, value: Int) = glUniform1i(loc, value)
	private fun setFloat(loc: Int, value: Float) = glUniform1f(loc, value)

	private fun setInts(loc: Int, values: IntArray) = when (values.size) {
		2 -> glUniform2i(loc, values[0], values[1])
		3 -> glUniform3i(loc, values[0], values[1], values[2])
		4 -> glUniform4i(loc, values[0], values[1], values[2], values[3])
		else -> LOGGER.error { "Unsupported int array size: ${values.size}" }
	}

	private fun setFloats(loc: Int, values: FloatArray) = when (values.size) {
		2 -> glUniform2f(loc, values[0], values[1])
		3 -> glUniform3f(loc, values[0], values[1], values[2])
		4 -> glUniform4f(loc, values[0], values[1], values[2], values[3])
		else -> LOGGER.error { "Unsupported float array size: ${values.size}" }
	}

	private fun setVector2i(loc: Int, vec2i: Vector2i) = setInts(loc, vec2i.toArray())
	private fun setVector3i(loc: Int, vec3i: Vector3i) = setInts(loc, vec3i.toArray())
	private fun setVector4i(loc: Int, vec4i: Vector4i) = setInts(loc, vec4i.toArray())

	private fun setVector2f(loc: Int, vec2f: Vector2f) = setFloats(loc, vec2f.toArray())
	private fun setVector3f(loc: Int, vec3f: Vector3f) = setFloats(loc, vec3f.toArray())
	private fun setVector4f(loc: Int, vec4f: Vector4f) = setFloats(loc, vec4f.toArray())

	private fun setMatrix3f(loc: Int, mat3f: Matrix3f) = MemoryStack.stackPush().use { stack ->
		val buffer = stack.mallocFloat(9)
		mat3f.get(buffer)
		glUniformMatrix3fv(loc, false, buffer)
	}

	private fun setMatrix4f(loc: Int, mat4f: Matrix4f) = MemoryStack.stackPush().use { stack ->
		val buffer = stack.mallocFloat(16)
		mat4f.get(buffer)
		glUniformMatrix4fv(loc, false, buffer)
	}

	override fun bind() {
		if (!compiled) {
			LOGGER.error { "Shader is not compiled: '$filepath'" }
			return
		}

		glUseProgram(programID)
	}

	override fun unbind() {
		if (!compiled) {
			LOGGER.error { "Shader is not compiled: '$filepath'" }
			return
		}

		glUseProgram(0)
	}

	override fun delete() {
		glDeleteProgram(programID)
	}

}