package org.emberstudios.core.thread

import io.github.oshai.kotlinlogging.KotlinLogging

private val LOGGER = KotlinLogging.logger("ThreadManager")

fun createThread(
	name: String,
	priority: Int = Thread.NORM_PRIORITY,
	daemon: Boolean = false,
	start: Boolean = false,
	action: () -> Unit,
): Thread {
	val thread = Thread {
		try {
			LOGGER.trace { "Thread [$name] started." }
			action()
		} catch (e: Exception) {
			LOGGER.error { "Thread [$name] encountered an error: ${e.message}" }
		} finally {
			LOGGER.trace { "Thread [$name] finished" }
		}
	}

	thread.name = name
	thread.priority = priority
	thread.isDaemon = daemon

	if (start)
		thread.start()

	return thread
}