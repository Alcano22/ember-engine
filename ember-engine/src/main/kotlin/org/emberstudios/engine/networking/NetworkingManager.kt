package org.emberstudios.engine.networking

import kotlinx.coroutines.*
import org.emberstudios.core.logger.getLogger
import org.emberstudios.networking.GameClient

object NetworkingManager {

	private val LOGGER = getLogger<NetworkingManager>()

	private var client: GameClient? = null
	private val networkingScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

	val hostAddress get() = client?.hostAddress ?: ""
	val tcpPort get() = client?.tcpPort ?: -1
	val udpPort get() = client?.udpPort ?: -1
	val isConnected get() = client?.isConnected ?: false

	var traceMessages: Boolean
		get() = client?.traceMessages ?: false
		set(value) { client?.traceMessages = value }

	val connectionDuration get() = client?.connectionDuration ?: -1f

	fun connect(host: String, tcpPort: Int, udpPort: Int, onConnected: suspend (Boolean) -> Unit = {}) {
		client = GameClient(host, tcpPort, udpPort)
		networkingScope.launch {
			client?.connect { success ->
				if (!success)
					client = null

				onConnected(success)
			}
		}
	}

	fun disconnect() {
		networkingScope.launch {
			client?.disconnect()
			client = null
		}
	}

	suspend fun measureLatency(): Long = withContext(Dispatchers.IO) {
		client?.measureLatency() ?: -1L
	}

	fun cleanup() {
		disconnect()
		networkingScope.cancel()
	}

}