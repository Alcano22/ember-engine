package org.emberstudios.engine.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.emberstudios.core.io.ResourceManager
import org.emberstudios.renderer.Texture
import org.emberstudios.renderer.loadTexture

object TextureSerializer : KSerializer<Texture> {
    override val descriptor = PrimitiveSerialDescriptor("Texture", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Texture) = encoder.encodeString(value.filepath)

    override fun deserialize(decoder: Decoder) = ResourceManager.loadTexture(decoder.decodeString())
}