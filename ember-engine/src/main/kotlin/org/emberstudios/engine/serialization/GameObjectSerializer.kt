package org.emberstudios.engine.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import org.emberstudios.core.logger.exitError
import org.emberstudios.core.logger.getLogger
import org.emberstudios.core.utils.GID
import org.emberstudios.engine.gameobject.GameObject
import org.emberstudios.engine.gameobject.component.Component
import org.emberstudios.engine.gameobject.component.Transform

object GameObjectSerializer : KSerializer<GameObject> {

    private val LOGGER = getLogger<GameObjectSerializer>()

    override val descriptor = buildClassSerialDescriptor("GameObject") {
        element<String>("name")
        element<GID>("gid")
        element<List<Component>>("components")
    }

    override fun serialize(encoder: Encoder, value: GameObject) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.name)
            encodeSerializableElement(descriptor, 1, GID.serializer(), value.gid)
            encodeSerializableElement(descriptor, 2, ListSerializer(Component.serializer()), value.components)
        }
    }

    override fun deserialize(decoder: Decoder): GameObject {
        return decoder.decodeStructure(descriptor) {
            var name = "GameObject"
            var gid = GID()
            var components = mutableListOf<Component>()

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> name = decodeStringElement(descriptor, index)
                    1 -> gid = decodeSerializableElement(descriptor, index, GID.serializer())
                    2 -> components = decodeSerializableElement(descriptor, index, ListSerializer(Component.serializer())).toMutableList()
                    CompositeDecoder.DECODE_DONE -> break
                    else -> LOGGER.exitError { "Unexpected index: $index" }
                }
            }

            components.sortBy { if (it is Transform) 0 else 1 }

            GameObject(name).apply {
                this.gid = gid
                this.components = components
            }
        }
    }
}