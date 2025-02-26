package org.emberstudios.networking

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import org.emberstudios.core.logger.getLogger

internal class GameServer(
	private val tcpPort: Int = 9115,
	private val udpPort: Int = 9125
) {

	companion object {
		val LOGGER = getLogger<GameServer>()
	}

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
				LOGGER.info { "TCP received: \"$line\" from ${socket.remoteAddress}" }
				when (line.trim().uppercase()) {
					"DISCONNECT" -> {
						output.writeStringUtf8("Goodbye!\n")
						break
					}
					else -> output.writeStringUtf8("Echo: $line\n")
				}
			}
		} catch (e: Exception) {
			LOGGER.error { "TCP client error: ${e.message}" }
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

fun main() = runBlocking {
	GameServer().start()
}