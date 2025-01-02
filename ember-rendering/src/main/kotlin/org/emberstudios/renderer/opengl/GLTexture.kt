package org.emberstudios.renderer.opengl

import org.emberstudios.core.io.ImageLoader
import org.emberstudios.core.logger.getLogger
import org.emberstudios.renderer.Texture
import org.lwjgl.opengl.GL45.*

internal class GLTexture(val filepath: String) : Texture {

    companion object {
        val LOGGER = getLogger<GLTexture>()
    }

    private var texID = 0

    private val width: Int
    private val height: Int

    init {
        val image = ImageLoader.load(filepath)

        texID = glCreateTextures(GL_TEXTURE_2D)
        bind()

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        var internalFormat = 0
        var dataFormat = 0
        when (image.channels) {
            3 -> {
                internalFormat = GL_RGB8
                dataFormat = GL_RGB
            }
            4 -> {
                internalFormat = GL_RGBA8
                dataFormat = GL_RGBA
            }
        }

        width = image.width
        height = image.height

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

    override fun bind() = glBindTexture(GL_TEXTURE_2D, texID)
    override fun unbind() = glBindTexture(GL_TEXTURE_2D, 0)
    override fun delete() = glDeleteTextures(texID)

    override fun activate(slot: Int) = glActiveTexture(GL_TEXTURE0 + slot)
    override fun deactivate(slot: Int) = glActiveTexture(0)

    override fun getID() = texID
    override fun getWidth() = width
    override fun getHeight() = height

}