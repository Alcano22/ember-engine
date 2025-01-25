package org.emberstudios.core.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.emberstudios.core.utils.GID

object GIDSerializer : KSerializer<GID> {
    override val descriptor = PrimitiveSerialDescriptor("GID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: GID) = encoder.encodeString(value.value)
    override fun deserialize(decoder: Decoder) = GID(decoder.decodeString())

}