package org.emberstudios.engine.serialization

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.emberstudios.core.serialization.*
import org.emberstudios.renderer.Texture
import org.joml.*

val jsonFormat = Json {
    prettyPrint = true
    classDiscriminator = "#type"
    serializersModule = SerializersModule {
        contextual(Texture::class, TextureSerializer)
        contextual(Vector2i::class, Vector2iSerializer)
        contextual(Vector3i::class, Vector3iSerializer)
        contextual(Vector4i::class, Vector4iSerializer)
        contextual(Vector2f::class, Vector2fSerializer)
        contextual(Vector3f::class, Vector3fSerializer)
        contextual(Vector4f::class, Vector4fSerializer)
    }
}
