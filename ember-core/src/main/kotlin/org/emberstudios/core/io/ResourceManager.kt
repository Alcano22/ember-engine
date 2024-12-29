package org.emberstudios.core.io

import org.emberstudios.core.logger.getLogger

object ResourceManager {

    private val LOGGER = getLogger<ResourceManager>()

    private val cache = mutableMapOf<String, Resource>()

    fun <T : Resource> load(path: String, loader: (String) -> T): T =
        cache.getOrPut(path) { loader(path) } as T

    fun clear() {
        cache.values.forEach {
            it.delete()
            LOGGER.trace { "Destroyed ${it::class.simpleName}" }
        }
        cache.clear()
    }

}

interface Resource {
    fun delete()
}