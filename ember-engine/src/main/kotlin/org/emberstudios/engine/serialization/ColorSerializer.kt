package org.emberstudios.engine.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import org.emberstudios.core.logger.exitError
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.util.Color

object ColorSerializer : KSerializer<Color> {

    private val LOGGER = getLogger<ColorSerializer>()

    override val descriptor = buildClassSerialDescriptor("Color") {
        element<Float>("r")
        element<Float>("g")
        element<Float>("b")
        element<Float>("a")
    }

    override fun serialize(encoder: Encoder, value: Color) = encoder.encodeStructure(descriptor) {
        encodeFloatElement(descriptor, 0, value.r)
        encodeFloatElement(descriptor, 1, value.g)
        encodeFloatElement(descriptor, 2, value.b)
        encodeFloatElement(descriptor, 3, value.a)
    }

    override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
        var r = 0f
        var g = 0f
        var b = 0f
        var a = 0f

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> r = decodeFloatElement(descriptor, index)
                1 -> g = decodeFloatElement(descriptor, index)
                2 -> b = decodeFloatElement(descriptor, index)
                3 -> a = decodeFloatElement(descriptor, index)
                CompositeDecoder.DECODE_DONE -> break
                else -> LOGGER.exitError { "Unexpected index: $index" }
            }
        }

        Color(r, g, b, a)
    }
}