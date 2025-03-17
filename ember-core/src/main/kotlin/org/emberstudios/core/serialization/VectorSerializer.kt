package org.emberstudios.core.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import org.emberstudios.core.logger.exitError
import org.emberstudios.core.logger.getLogger
import org.joml.*

/**
 * Serializer for [Vector2i].
 */
object Vector2iSerializer : KSerializer<Vector2i> {
    private val LOGGER = getLogger<Vector2iSerializer>()

    override val descriptor = buildClassSerialDescriptor("Vector2i") {
        element<Int>("x")
        element<Int>("y")
    }

    override fun serialize(encoder: Encoder, value: Vector2i) = encoder.encodeStructure(descriptor) {
        encodeIntElement(descriptor, 0, value.x)
        encodeIntElement(descriptor, 1, value.y)
    }

    override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
        var x = 0
        var y = 0

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> x = decodeIntElement(descriptor, index)
                1 -> y = decodeIntElement(descriptor, index)
                CompositeDecoder.DECODE_DONE -> break
                else -> LOGGER.exitError { "Unexpected index: $index" }
            }
        }

        Vector2i(x, y)
    }
}

object Vector3iSerializer : KSerializer<Vector3i> {
    private val LOGGER = getLogger<Vector3iSerializer>()

    override val descriptor = buildClassSerialDescriptor("Vector3i") {
        element<Int>("x")
        element<Int>("y")
        element<Int>("z")
    }

    override fun serialize(encoder: Encoder, value: Vector3i) = encoder.encodeStructure(descriptor) {
        encodeIntElement(descriptor, 0, value.x)
        encodeIntElement(descriptor, 1, value.y)
        encodeIntElement(descriptor, 2, value.z)
    }

    override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
        var x = 0
        var y = 0
        var z = 0

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> x = decodeIntElement(descriptor, index)
                1 -> y = decodeIntElement(descriptor, index)
                2 -> z = decodeIntElement(descriptor, index)
                CompositeDecoder.DECODE_DONE -> break
                else -> LOGGER.exitError { "Unexpected index: $index" }
            }
        }

        Vector3i(x, y, z)
    }
}

object Vector4iSerializer : KSerializer<Vector4i> {
    private val LOGGER = getLogger<Vector4iSerializer>()

    override val descriptor = buildClassSerialDescriptor("Vector4i") {
        element<Int>("x")
        element<Int>("y")
        element<Int>("z")
        element<Int>("w")
    }

    override fun serialize(encoder: Encoder, value: Vector4i) = encoder.encodeStructure(descriptor) {
        encodeIntElement(descriptor, 0, value.x)
        encodeIntElement(descriptor, 1, value.y)
        encodeIntElement(descriptor, 2, value.z)
        encodeIntElement(descriptor, 3, value.w)
    }

    override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
        var x = 0
        var y = 0
        var z = 0
        var w = 0

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> x = decodeIntElement(descriptor, index)
                1 -> y = decodeIntElement(descriptor, index)
                2 -> z = decodeIntElement(descriptor, index)
                3 -> w = decodeIntElement(descriptor, index)
                CompositeDecoder.DECODE_DONE -> break
                else -> LOGGER.exitError { "Unexpected index: $index" }
            }
        }

        Vector4i(x, y, z, w)
    }
}

object Vector2fSerializer : KSerializer<Vector2f> {
    private val LOGGER = getLogger<Vector2fSerializer>()

    override val descriptor = buildClassSerialDescriptor("Vector2f") {
        element<Float>("x")
        element<Float>("y")
    }

    override fun serialize(encoder: Encoder, value: Vector2f) = encoder.encodeStructure(descriptor) {
        encodeFloatElement(descriptor, 0, value.x)
        encodeFloatElement(descriptor, 1, value.y)
    }

    override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
        var x = 0f
        var y = 0f

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> x = decodeFloatElement(descriptor, index)
                1 -> y = decodeFloatElement(descriptor, index)
                CompositeDecoder.DECODE_DONE -> break
                else -> LOGGER.exitError { "Unexpected index: $index" }
            }
        }

        Vector2f(x, y)
    }
}

object Vector3fSerializer : KSerializer<Vector3f> {
    private val LOGGER = getLogger<Vector3fSerializer>()

    override val descriptor = buildClassSerialDescriptor("Vector3f") {
        element<Float>("x")
        element<Float>("y")
        element<Float>("z")
    }

    override fun serialize(encoder: Encoder, value: Vector3f) = encoder.encodeStructure(descriptor) {
        encodeFloatElement(descriptor, 0, value.x)
        encodeFloatElement(descriptor, 1, value.y)
        encodeFloatElement(descriptor, 2, value.z)
    }

    override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
        var x = 0f
        var y = 0f
        var z = 0f

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> x = decodeFloatElement(descriptor, index)
                1 -> y = decodeFloatElement(descriptor, index)
                2 -> z = decodeFloatElement(descriptor, index)
                CompositeDecoder.DECODE_DONE -> break
                else -> LOGGER.exitError { "Unexpected index: $index" }
            }
        }

        Vector3f(x, y, z)
    }
}

object Vector4fSerializer : KSerializer<Vector4f> {
    private val LOGGER = getLogger<Vector4fSerializer>()

    override val descriptor = buildClassSerialDescriptor("Vector4f") {
        element<Float>("x")
        element<Float>("y")
        element<Float>("z")
        element<Float>("w")
    }

    override fun serialize(encoder: Encoder, value: Vector4f) = encoder.encodeStructure(descriptor) {
        encodeFloatElement(descriptor, 0, value.x)
        encodeFloatElement(descriptor, 1, value.y)
        encodeFloatElement(descriptor, 2, value.z)
        encodeFloatElement(descriptor, 3, value.w)
    }

    override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
        var x = 0f
        var y = 0f
        var z = 0f
        var w = 0f

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> x = decodeFloatElement(descriptor, index)
                1 -> y = decodeFloatElement(descriptor, index)
                2 -> z = decodeFloatElement(descriptor, index)
                3 -> w = decodeFloatElement(descriptor, index)
                CompositeDecoder.DECODE_DONE -> break
                else -> LOGGER.exitError { "Unexpected index: $index" }
            }
        }

        Vector4f(x, y, z, w)
    }
}
