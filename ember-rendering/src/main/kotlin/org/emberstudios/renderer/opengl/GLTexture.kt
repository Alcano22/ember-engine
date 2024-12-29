package org.emberstudios.renderer.opengl

import org.emberstudios.core.io.ImageLoader
import org.emberstudios.core.logger.getLogger
import org.emberstudios.renderer.Texture
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.*

internal class GLTexture(val filepath: String) : Texture {

    companion object {
        val LOGGER = getLogger<GLTexture>()
    }

    private var texID = 0

    private val width: Int
    private val height: Int

    init {
        val image = ImageLoader.load(filepath)

        texID = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, texID)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        val format = if (image.channels == 4) GL_RGBA else GL_RGB
        width = image.width
        height = image.height

        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            format,
            width,
            height,
            0,
            format,
            GL_UNSIGNED_BYTE,
            image.data
        )

        glGenerateMipmap(GL_TEXTURE_2D)
    }

    override fun bind() {
        glBindTexture(GL_TEXTURE_2D, texID)
    }

    override fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    override fun delete() {
        glDeleteTextures(texID)
    }

    override fun getID() = texID
    override fun getWidth() = width
    override fun getHeight() = height

}