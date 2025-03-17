package org.emberstudios.core.utils

import kotlinx.serialization.Serializable
import org.emberstudios.core.serialization.GIDSerializer
import java.util.UUID

/**
 * A globally unique identifier.
 */
@Serializable(with = GIDSerializer::class)
class GID(val value: String = UUID.randomUUID().toString()) {
    override fun equals(other: Any?) =
        other is GID &&
        value == other.value

    override fun toString() = value
}