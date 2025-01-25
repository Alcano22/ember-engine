package org.emberstudios.renderer.opengl

import org.emberstudios.core.io.ImageLoader
import org.emberstudios.core.logger.getLogger
import org.emberstudios.renderer.Texture
import org.lwjgl.opengl.GL45.*

internal class GLTexture private constructor(
    override val texID: Int,
    width: Int,
    height: Int,
    uv: FloatArray = floatArrayOf(0f, 0f, 1f, 1f),
    override val filepath: String
) : Texture {

    override var width: Int
    override var height: Int
    override var uv: FloatArray

    companion object {
        val LOGGER = getLogger<GLTexture>()
    }

    init {
        this.width = width
        this.height = height
        this.uv = uv
    }

    constructor(filepath: String)
            : this(glCreateTextures(GL_TEXTURE_2D), 0, 0, floatArrayOf(0f, 0f, 1f, 1f), filepath) {
        val image = ImageLoader.load(filepath)

        bind()

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        val (internalFormat, dataFormat) = when (image.channels) {
            3 -> GL_RGB8 to GL_RGB
            4 -> GL_RGBA8 to GL_RGBA
            else -> {
                LOGGER.error { "Invalid channel amount!" }
                return
            }
        }

        this.width = image.width
        this.height = image.height

        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            internalFormat,
            width,
            height,
            0,
            dataFormat,
            GL_UNSIGNED_BYTE,
            image.data
        )

        glGenerateMipmap(GL_TEXTURE_2D)

        ImageLoader.free(image.data)
        unbind()
    }

    override fun subTexture(x: Int, y: Int, width: Int, height: Int): Texture {
        val u0 = x.toFloat() / this.width
        val v0 = (this.height - (y + height)).toFloat() / this.height
        val u1 = (x + width).toFloat() / this.width
        val v1 = (this.height - y).toFloat() / this.height
        val uvCoords = floatArrayOf(u0, v0, u1, v1)

        return GLTexture(texID, width, height, uvCoords, "subtex($filepath)")
    }

    override fun bind() = glBindTexture(GL_TEXTURE_2D, texID)
    override fun unbind() = glBindTexture(GL_TEXTURE_2D, 0)
    override fun delete() = glDeleteTextures(texID)

    override fun activate(slot: Int) = glActiveTexture(GL_TEXTURE0 + slot)
    override fun deactivate(slot: Int) = glActiveTexture(0)

}