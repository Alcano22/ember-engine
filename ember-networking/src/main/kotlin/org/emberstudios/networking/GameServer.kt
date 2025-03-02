package org.emberstudios.networking

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import org.emberstudios.core.logger.CORE_LOGGER
import org.emberstudios.core.logger.getLogger
import org.emberstudios.networking.config.ServerConfig
import org.emberstudios.networking.config.loadConfig

internal class GameServer(config: ServerConfig) {

	companion object {
		val LOGGER = getLogger<GameServer>()
	}

	private val tcpPort = config.tcpPort
	private val udpPort = config.udpPort

	suspend fun start(): Unit = coroutineScope {
		launch { startTCPServer() }
		launch { startUDPServer() }
	}

	private suspend fun startTCPServer() = withContext(Dispatchers.IO) {
		val selectorManager = SelectorManager(Dispatchers.IO)
		val serverSocket = aSocket(selectorManager).tcp().bind("0.0.0.0", tcpPort)
		LOGGER.info { "TCP server started on port $tcpPort" }
		while (true) {
			val socket = serverSocket.accept()
			LOGGER.info { "TCP client connected: ${socket.remoteAddress}" }
			launch { handleTCPClient(socket) }
		}
	}

	private suspend fun handleTCPClient(socket: Socket) = withContext(Dispatchers.IO) {
		val input = socket.openReadChannel()
		val output = socket.openWriteChannel(autoFlush = true)
		output.writeStringUtf8("Welcome!\n")
		try {
			while (true) {
				val line = input.readUTF8Line() ?: break
				when {
					line == "DISCONNECT" -> {
						output.writeStringUtf8("Goodbye!\n")
						LOGGER.info { "Disconnected ${socket.remoteAddress}" }
						break
					}
					line.startsWith("PING:") -> {
						val timestamp = line.substringAfter("PING:")
						output.writeStringUtf8("PONG:$timestamp\n")
					}
					else -> output.writeStringUtf8("Echo: $line\n")
				}
			}
		} catch (e: Exception) {
			if (e is CancellationException) {
				throw e
			} else if (e.message?.contains("Connection reset")!!) {
				LOGGER.warn { "TCP client connection reset (likely due to graceful disconnect)." }
			} else {
				LOGGER.error { "TCP client error: ${e.message}" }
			}
		} finally {
			LOGGER.info { "TCP client disconnected: ${socket.remoteAddress}" }
			socket.close()
		}
	}


	private suspend fun startUDPServer() = withContext(Dispatchers.IO) {
		val selectorManager = SelectorManager(Dispatchers.IO)
		val udpSocket = aSocket(selectorManager).udp().bind(InetSocketAddress("0.0.0.0", udpPort))
		LOGGER.info { "UDP server started on port $udpPort" }
		while (true) {
			val datagram = udpSocket.receive()
			val receivedText = datagram.packet.readText()
			LOGGER.info { "UDP received from ${datagram.address}: $receivedText" }
			try {
				val netTransform = Json.decodeFromString<NetTransform>(receivedText)
				LOGGER.info { "Parsed transform: $netTransform" }
			} catch (e: Exception) {
				LOGGER.error { "Failed to decode transform JSON: ${e.message}" }
			}
		}
	}

}

suspend fun main() {
	val config = loadConfig()
	val server = GameServer(config)
	val serverJob = CoroutineScope(Dispatchers.IO).launch { server.start() }

	while (true) {
		val input = readlnOrNull() ?: break
		when (input.trim().lowercase()) {
			"stop" -> {
				CORE_LOGGER.info { "Stopping the server..." }
				serverJob.cancelAndJoin()
				break
			}
			else -> CORE_LOGGER.error { "Unknown command: $input" }
		}
	}
}