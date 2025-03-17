package org.emberstudios.core.thread

import io.github.oshai.kotlinlogging.KotlinLogging

private val LOGGER = KotlinLogging.logger("ThreadManager")

/**
 * Creates a new thread with the specified name, priority, daemon status, and action.
 *
 * @param name The name of the thread.
 * @param priority The priority of the thread.
 * @param daemon Whether the thread should be a daemon thread.
 * @param start Whether the thread should be started immediately.
 * @param action The action to be performed by the thread.
 *
 * @return The created thread.
 */
fun createThread(
	name: String,
	priority: Int = Thread.NORM_PRIORITY,
	daemon: Boolean = false,
	start: Boolean = false,
	action: () -> Unit,
	onFinish: () -> Unit = {}
): Thread {
	val thread = Thread {
		try {
			LOGGER.trace { "Thread [$name] started." }
			action()
		} catch (e: Exception) {
			LOGGER.error { "Thread [$name] encountered an error: ${e.message}" }
		} finally {
			LOGGER.trace { "Thread [$name] finished" }
			onFinish()
		}
	}

	thread.name = name
	thread.priority = priority
	thread.isDaemon = daemon

	if (start)
		thread.start()

	return thread
}