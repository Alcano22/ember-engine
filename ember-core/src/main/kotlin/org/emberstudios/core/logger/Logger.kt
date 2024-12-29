package org.emberstudios.core.logger

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.system.exitProcess

val CORE_LOGGER = KotlinLogging.logger("Core")

fun KLogger.exitError(message: () -> Any?): Nothing {
	error(message)
	exitProcess(-1)
}

inline fun <reified T> getLogger() = KotlinLogging.logger(T::class.simpleName ?: "<anonymous>")