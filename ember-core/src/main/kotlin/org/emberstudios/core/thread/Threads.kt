package org.emberstudios.core.thread

import org.emberstudios.core.logger.CORE_LOGGER

fun createThread(
	name: String,
	priority: Int = Thread.NORM_PRIORITY,
	daemon: Boolean = false,
	start: Boolean = false,
	action: () -> Unit,
): Thread {
	val thread = Thread {
		try {
			CORE_LOGGER.info { "Thread [$name] started." }
			action()
		} catch (e: Exception) {
			CORE_LOGGER.error { "Thread [$name] encountered an error: ${e.message}" }
		} finally {
			CORE_LOGGER.info { "Thread [$name] finished" }
		}
	}

	thread.name = name
	thread.priority = priority
	thread.isDaemon = daemon

	if (start)
		thread.start()

	return thread
}