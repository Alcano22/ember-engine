package org.emberstudios.core.logger

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.system.exitProcess

/**
 * The logger for the core module.
 */
val CORE_LOGGER = KotlinLogging.logger("Core")

/**
 * Logs an error message and exits the program with a status code of -1.
 *
 * @param message The message to log.
 *
 * @return Nothing.
 */
fun KLogger.exitError(message: () -> Any?): Nothing {
	error(message)
	exitProcess(-1)
}

/**
 * Returns a logger for the specified class.
 */
inline fun <reified T> getLogger() = KotlinLogging.logger(T::class.simpleName ?: "<anonymous>")