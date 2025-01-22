package org.emberstudios.core.utils

import java.util.UUID

class GID {
    val value = UUID.randomUUID().toString()

    override fun equals(other: Any?) =
        other is GID &&
        value == other.value

    override fun toString() = value
}