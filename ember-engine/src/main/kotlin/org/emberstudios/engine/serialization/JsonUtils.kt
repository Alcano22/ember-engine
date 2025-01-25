package org.emberstudios.engine.serialization

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.gameobject.GameObject
import org.emberstudios.engine.gameobject.component.Component
import org.emberstudios.engine.gameobject.component.PlayerMove
import org.emberstudios.engine.gameobject.component.SpriteRenderer
import org.emberstudios.engine.gameobject.component.Transform
import org.emberstudios.engine.util.Color
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
