package org.emberstudios.renderer.opengl

import org.emberstudios.core.logger.exitError
import org.emberstudios.core.logger.getLogger
import org.emberstudios.core.math.toArray
import org.emberstudios.renderer.Shader
import org.joml.*
import org.lwjgl.opengl.GL20.*
import java.io.File

internal class GLShader : Shader {

	companion object {
		const val TYPE_PREFIX = "#type"

		val LOGGER = getLogger<GLShader>()
	}

	private var programID = 0

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
		when (value) {
			is Boolean -> setInt(name, if (value) 1 else 0)
			is Int -> setInt(name, value)
			is Float -> setFloat(name, value)
			is IntArray -> setInts(name, value)
			is FloatArray -> setFloats(name, value)
			is Vector2i -> setVector2i(name, value)
			is Vector3i -> setVector3i(name, value)
			is Vector4i -> setVector4i(name, value)
			is Vector2f -> setVector2f(name, value)
			is Vector3f -> setVector3f(name, value)
			is Vector4f -> setVector4f(name, value)
			else -> LOGGER.exitError {
				val typename = value!!::class.simpleName
				"Uniform type '$typename' is not supported!"
			}
		}
	}

	private fun setInt(name: String, value: Int) {
		val loc = glGetUniformLocation(programID, name)
		assertUniformLocation(loc, name)
		glUniform1i(loc, value)
	}

	private fun setFloat(name: String, value: Float) {
		val loc = glGetUniformLocation(programID, name)
		assertUniformLocation(loc, name)
		glUniform1f(loc, value)
	}

	private fun setInts(name: String, values: IntArray) {
		val loc = glGetUniformLocation(programID, name)
		assertUniformLocation(loc, name)
		when (values.size) {
			2 -> glUniform2i(loc, values[0], values[1])
			3 -> glUniform3i(loc, values[0], values[1], values[2])
			4 -> glUniform4i(loc, values[0], values[1], values[2], values[3])
			else -> LOGGER.error { "Unsupported int array size: ${values.size}" }
		}
	}

	private fun setFloats(name: String, values: FloatArray) {
		val loc = glGetUniformLocation(programID, name)
		assertUniformLocation(loc, name)
		when (values.size) {
			2 -> glUniform2f(loc, values[0], values[1])
			3 -> glUniform3f(loc, values[0], values[1], values[2])
			4 -> glUniform4f(loc, values[0], values[1], values[2], values[3])
			else -> LOGGER.error { "Unsupported float array size: ${values.size}" }
		}
	}

	private fun setVector2i(name: String, vec2i: Vector2i) = setInts(name, vec2i.toArray())
	private fun setVector3i(name: String, vec3i: Vector3i) = setInts(name, vec3i.toArray())
	private fun setVector4i(name: String, vec4i: Vector4i) = setInts(name, vec4i.toArray())

	private fun setVector2f(name: String, vec2f: Vector2f) = setFloats(name, vec2f.toArray())
	private fun setVector3f(name: String, vec3f: Vector3f) = setFloats(name, vec3f.toArray())
	private fun setVector4f(name: String, vec4f: Vector4f) = setFloats(name, vec4f.toArray())

	private fun assertUniformLocation(loc: Int, name: String) {
		if (loc == -1)
			LOGGER.error { "Invalid uniform location: $name" }
	}

	override fun bind() {
		glUseProgram(programID)
	}

	override fun unbind() {
		glUseProgram(0)
	}

	override fun delete() {
		glDeleteProgram(programID)
	}

}